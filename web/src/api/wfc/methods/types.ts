/**
 * Type contract for the WildFireChat adapter's RPC layer.
 *
 * Phase 0.5 (this file): Methods is `Record<string, AnyFn>`. UI calls
 *   callApi('anyName', ...) → Promise<any>. No name- or arg-level checking.
 *
 * Phase 1 (future): once we start porting telegram-tt's per-method signatures
 *   into web/src/api/wfc/methods/index.ts (matching the original
 *   typeof * as methods from './index' pattern), this file will switch back
 *   to `export type Methods = typeof methods;` and full type-safety returns.
 *
 * See ARCHITECTURE.md and the README in this directory for the migration plan.
 */

/* eslint-disable @typescript-eslint/no-explicit-any */

type AnyFn = (...args: any[]) => any;

export type Methods = Record<string, AnyFn>;

export type MethodArgs<N extends keyof Methods> = Parameters<Methods[N]>;

export type MethodResponse<N extends keyof Methods> = ReturnType<Methods[N]>;
