/**
 * Local DB shim — minimal placeholder so type imports succeed.
 *
 * The original Telegram-tt `localDb` cached MTProto entity classes for
 * downloading media. WildFireChat manages its own caches (IndexedDB inside
 * the SDK), so this module's job is mostly to satisfy `multitab.ts`'s
 * cross-tab broadcast contract until Phase 2 wires real media loading.
 */

/* eslint-disable @typescript-eslint/no-explicit-any */

export type StoryRepairInfo = {
  type: 'story';
  peerId: string;
  id: number;
};

export type MessageRepairInfo = {
  type: 'message';
  peerId: string;
  id: number;
};

export type WebPageRepairInfo = {
  type: 'webPage';
  url: string;
};

export type RepairInfo = {
  localRepairInfo?: StoryRepairInfo | MessageRepairInfo | WebPageRepairInfo;
};

export interface LocalDb {
  [key: string]: any;
}

export const localDb: LocalDb = {};

export function broadcastLocalDbUpdateFull(): void {
  // Phase 2 will broadcast cache updates to other tabs via BroadcastChannel.
}

export function clearLocalDb(): void {
  // no-op
}
