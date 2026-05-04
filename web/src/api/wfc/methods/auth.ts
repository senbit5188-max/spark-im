/**
 * Phase 1 — auth method adapter (REST-only slice).
 *
 * Goal of this slice: take the user from the login form into the
 * authenticated UI shell using ONLY the spark-im REST endpoint
 *   POST {APP_SERVER}/login_pwd  with { mobile, password, platform, clientId }
 *   -> response { code: 0, result: { userId, token } }
 *
 * The WildFireChat WebSocket layer (wfc.connect / message subscriptions)
 * is intentionally NOT wired here — that's Phase 2 work, where we'll
 * fold the vendored SDK under wfc-sdk/ into webpack's module graph
 * (it has its own module-resolution quirks that need separate care).
 *
 * For Phase 1 we just need to prove:
 *   1. UI renders the account+password form (replaces QR loading spinner).
 *   2. Submitting valid credentials moves the UI into authorizationStateReady.
 *   3. userId+token are persisted so Phase 2 wfc.connect() can resume.
 */

import type { ApiUpdate } from '../../types';

// spark-im backend lives behind chat.telvoro.top (the Java app-server is
// reverse-proxied via nginx). Hard-coded so we don't depend on EnvironmentPlugin.
const APP_SERVER = 'https://chat.telvoro.top';
const PLATFORM_WEB = 5;
const TOKEN_STORAGE_KEY = 'sparkIm.authToken';
const USER_ID_STORAGE_KEY = 'sparkIm.userId';
const ACCOUNT_STORAGE_KEY = 'sparkIm.account';

let onUpdate: ((update: ApiUpdate) => void) | undefined;
let pendingAccount: string | undefined;

export function setAuthOnUpdate(cb: (update: ApiUpdate) => void): void {
  onUpdate = cb;
}

function emit(update: ApiUpdate): void {
  onUpdate?.(update);
}

function generateClientId(): string {
  // wfc-sdk uses a 16-byte random clientId. For Phase 1 a uuid-ish substitute
  // is enough — backend accepts any non-empty string at /login_pwd.
  return 'sparkim-web-' + Math.random().toString(36).slice(2, 10) + Date.now().toString(36);
}

function getOrCreateClientId(): string {
  try {
    const existing = localStorage.getItem('sparkIm.clientId');
    if (existing) return existing;
    const fresh = generateClientId();
    localStorage.setItem('sparkIm.clientId', fresh);
    return fresh;
  } catch {
    return generateClientId();
  }
}

/**
 * Called once at adapter init. Pushes the UI from its default
 * (authorizationStateUnknown / QR-loading-spinner) into the
 * account-entry page so the user has something to type into.
 */
export function emitInitialAuthState(): void {
  emit({
    '@type': 'updateAuthorizationState',
    authorizationState: 'authorizationStateWaitPhoneNumber',
  } as unknown as ApiUpdate);
}

export function provideAuthPhoneNumber(account: string): void {
  pendingAccount = account;
  emit({
    '@type': 'updateAuthorizationState',
    authorizationState: 'authorizationStateWaitPassword',
  } as unknown as ApiUpdate);
}

export function provideAuthCode(_code: string): void {
  emit({
    '@type': 'updateAuthorizationState',
    authorizationState: 'authorizationStateWaitPassword',
  } as unknown as ApiUpdate);
}

export async function provideAuthPassword(password: string): Promise<void> {
  if (!pendingAccount) {
    emit({
      '@type': 'updateAuthorizationError',
      message: '请先输入账号',
    } as unknown as ApiUpdate);
    emit({
      '@type': 'updateAuthorizationState',
      authorizationState: 'authorizationStateWaitPhoneNumber',
    } as unknown as ApiUpdate);
    return;
  }

  try {
    const clientId = getOrCreateClientId();

    const res = await fetch(`${APP_SERVER}/login_pwd`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      credentials: 'include',
      body: JSON.stringify({
        mobile: pendingAccount,
        password,
        platform: PLATFORM_WEB,
        clientId,
      }),
    });

    const body = await res.json();

    if (body.code !== 0 || !body.result) {
      emit({
        '@type': 'updateAuthorizationError',
        message: body.message || '账号或密码错误',
      } as unknown as ApiUpdate);
      return;
    }

    const result = body.result as { userId: string; token: string };

    try {
      localStorage.setItem(USER_ID_STORAGE_KEY, result.userId);
      localStorage.setItem(TOKEN_STORAGE_KEY, result.token);
      localStorage.setItem(ACCOUNT_STORAGE_KEY, pendingAccount);
      const authToken = res.headers.get('authToken') || res.headers.get('authtoken');
      if (authToken) {
        localStorage.setItem('sparkIm.serverAuthToken', authToken);
      }
    } catch {
      // localStorage may be unavailable in some sandboxed previews; ignore.
    }

    emit({
      '@type': 'updateAuthorizationState',
      authorizationState: 'authorizationStateReady',
    } as unknown as ApiUpdate);

    pendingAccount = undefined;
  } catch (err) {
    emit({
      '@type': 'updateAuthorizationError',
      message: err instanceof Error ? err.message : String(err),
    } as unknown as ApiUpdate);
  }
}

export function restartAuth(): void {
  pendingAccount = undefined;
  try {
    localStorage.removeItem(USER_ID_STORAGE_KEY);
    localStorage.removeItem(TOKEN_STORAGE_KEY);
    localStorage.removeItem(ACCOUNT_STORAGE_KEY);
  } catch {
    // ignore
  }
  emit({
    '@type': 'updateAuthorizationState',
    authorizationState: 'authorizationStateWaitPhoneNumber',
  } as unknown as ApiUpdate);
}
