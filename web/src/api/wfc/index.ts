/**
 * Star Fire IM Web — WildFireChat adapter facade.
 *
 * This is the API entry point that the React UI consumes. Originally this
 * pointed at `gramjs/` (Telegram MTProto). We have removed that layer and
 * are replacing it with a thin adapter on top of the WildFireChat JS SDK
 * (vendored under /wfc-sdk/sdk/).
 *
 * Phase 1 status: STUB. Most methods throw `NotImplementedError` so the
 * tree compiles. As we wire each capability, replace stubs with real
 * implementations under /web/src/api/wfc/methods/.
 *
 * See ARCHITECTURE.md at repo root for the migration roadmap.
 */

import type { ApiInitialArgs, OnApiUpdate } from '../types';

/* eslint-disable @typescript-eslint/no-unused-vars, @typescript-eslint/no-explicit-any */

export class NotImplementedError extends Error {
  constructor(method: string) {
    super(`[wfc-adapter] '${method}' not implemented yet — see ARCHITECTURE.md`);
    this.name = 'NotImplementedError';
  }
}

let onUpdate: OnApiUpdate | undefined;

export function initApi(_onUpdate: OnApiUpdate, _initialArgs: ApiInitialArgs) {
  onUpdate = _onUpdate;
  // Phase 1 will wire wfc.attach() + wfc.connect() + wfc event subscriptions here.
  return Promise.resolve();
}

// Phase 0.5 stub: typed as `any` to keep the UI compiling. Phase 1 will replace
// this with proper Methods/Args/Return type tables (see methods/types.ts roadmap).
export function callApi<T extends string>(method: T, ..._args: any[]): Promise<any> {
  return Promise.reject(new NotImplementedError(method));
}

export function callApiLocal<T extends string>(method: T, ..._args: any[]): any {
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
