/**
 * Star Fire IM Web — WildFireChat adapter facade.
 *
 * This is the API entry point that the React UI consumes. Originally this
 * pointed at `gramjs/` (Telegram MTProto). We have removed that layer and
 * are replacing it with a thin adapter on top of the WildFireChat JS SDK
 * (vendored under /wfc-sdk/sdk/).
 *
 * Phase 1 status: PARTIAL. Auth methods (provideAuthPhoneNumber /
 * provideAuthPassword) are wired to the spark-im backend so the user
 * can actually log in. Conversation / message methods still throw
 * NotImplementedError — Phase 2+ will fill those in.
 *
 * See ARCHITECTURE.md at repo root for the migration roadmap.
 */

import type { ApiInitialArgs, OnApiUpdate } from '../types';

import {
  emitInitialAuthState,
  provideAuthCode,
  provideAuthPassword,
  provideAuthPhoneNumber,
  restartAuth,
  setAuthOnUpdate,
} from './methods/auth';

/* eslint-disable @typescript-eslint/no-unused-vars, @typescript-eslint/no-explicit-any */

export class NotImplementedError extends Error {
  constructor(method: string) {
    super(`[wfc-adapter] '${method}' not implemented yet — see ARCHITECTURE.md`);
    this.name = 'NotImplementedError';
  }
}

let onUpdate: OnApiUpdate | undefined;

// Methods that the UI may invoke via callApi('xxx', ...). Anything not
// listed here falls through to a NotImplementedError so we surface gaps
// loudly during Phase 1+ development.
const methods: Record<string, (...args: any[]) => any> = {
  provideAuthPhoneNumber: (phoneNumber: string) => provideAuthPhoneNumber(phoneNumber),
  provideAuthCode: (code: string) => provideAuthCode(code),
  provideAuthPassword: (password: string) => provideAuthPassword(password),
  restartAuth: () => restartAuth(),
};

export function initApi(_onUpdate: OnApiUpdate, _initialArgs: ApiInitialArgs) {
  onUpdate = _onUpdate;
  setAuthOnUpdate(_onUpdate);

  // Kick the UI out of its loading-spinner default and into the
  // account-entry page immediately on boot.
  emitInitialAuthState();

  return Promise.resolve();
}

export function callApi<T extends string>(method: T, ...args: any[]): Promise<any> {
  const fn = methods[method];
  if (fn) {
    try {
      return Promise.resolve(fn(...args));
    } catch (err) {
      return Promise.reject(err);
    }
  }
  return Promise.reject(new NotImplementedError(method));
}

export function callApiLocal<T extends string>(method: T, ...args: any[]): any {
  const fn = methods[method];
  if (fn) {
    return fn(...args);
  }
  throw new NotImplementedError(method);
}

export function cancelApiProgress(_progressCallback: unknown): void {
  // no-op until media transfer adapter exists
}

export function cancelApiProgressMaster(_progressCallback: unknown): void {
  // no-op
}

export function handleMethodCallback(_data: unknown): void {
  // no-op (was used by the worker bridge in MTProto layer; not relevant for wfc)
}

export function handleMethodResponse(_data: unknown): void {
  // no-op
}

export function updateLocalDb(_name?: string, _prop?: string, _value?: unknown): void {
  // wfc has its own indexedDb cache; no parallel localDb
}

export function updateFullLocalDb(_localDb?: unknown): void {
  // wfc has its own indexedDb cache; no parallel localDb
}

export function setShouldEnableDebugLog(_enabled: boolean): void {
  // wfc has its own debug toggles
}

// Re-export types for convenience
export type { ApiInitialArgs, OnApiUpdate } from '../types';
