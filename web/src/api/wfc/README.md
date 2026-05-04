# `web/src/api/wfc/` — WildFireChat adapter

This directory is the **only** API surface the React UI sees.

## Mission

Replace Telegram-tt's MTProto API (`src/api/gramjs/`, removed) with a thin
adapter on top of the **WildFireChat JS SDK** (vendored under
`/wfc-sdk/sdk/`).

The UI imports `callApi('methodName', args)` — Phase 1 wires each
`methodName` to a `wfc.X(...)` call.

## Method coverage roadmap

The original gramjs adapter exposed ~421 distinct `callApi` methods. The
WildFireChat SDK exposes ~279 distinct `wfc.X` methods. Mapping is **not
1:1**:

### Group 1 — clean 1:1 mapping (~60% of UI flows)

| UI callApi         | wfc method                             |
| ------------------ | -------------------------------------- |
| fetchChats         | wfc.getConversationList                |
| fetchMessages      | wfc.getMessages                        |
| sendMessage        | wfc.sendConversationMessage            |
| createGroup        | wfc.createGroup                        |
| addChatMembers     | wfc.addGroupMembers                    |
| removeChatMember   | wfc.kickoffGroupMembers                |
| fetchProfilePhotos | wfc.getUserPortrait                    |
| fetchCurrentUser   | wfc.getUserInfo(wfc.getUserId())       |
| login              | wfc.connect(userId, token)             |
| logOut             | wfc.disconnect()                       |
| updateProfile      | wfc.modifyMyInfo                       |
| markMessageListRead| wfc.clearConversationUnreadStatus      |

(Phase 1 will produce the full mapping table.)

### Group 2 — needs UI degradation (Telegram-only features)

These have no WildFireChat equivalent. UI surfaces will be hidden via
build-time feature flags or CSS:

- **Channels** (broadcast-only chats) — wfc has groups but no channels
- **Stickers** (.tgs animated stickers, sticker packs, premium stickers)
- **Stars / TON wallet / Premium** payments
- **Secret chats** (end-to-end encryption — wfc may add later)
- **Stories** (24h ephemeral content)
- **Bots inline mode / scenes / web apps**
- **Boosts / giveaways**
- **Message reactions** (only if wfc supports them — current vendored SDK does not)
- **Saved messages**, **scheduled messages**, **silent messages**

### Group 3 — needs WildFireChat backend extension

These are core IM features the backend may need to add:

- Edit message content (`editMessage`)
- Reply context for forwarded messages
- Read receipts at per-message granularity
- Online/typing indicators (basic wfc supports user-online; need typing)

## File layout (target)

```
wfc/
├── index.ts                  ← public façade (callApi, initApi, ...)
├── client.ts                 ← wfc.attach + wfc.connect + status events
├── methods/
│   ├── auth.ts               ← login, logout, register
│   ├── chats.ts              ← fetchChats, fetchChat, createGroup
│   ├── messages.ts           ← fetchMessages, sendMessage, edit, delete
│   ├── users.ts              ← fetchUsers, search, contacts
│   ├── settings.ts           ← updateProfile, notifications, devices
│   └── ...
├── builders/
│   ├── apiChat.ts            ← Conversation → ApiChat
│   ├── apiMessage.ts         ← Message → ApiMessage
│   └── apiUser.ts            ← UserInfo → ApiUser
└── updates/
    ├── newMessage.ts         ← OnReceiveMessage event → ApiUpdate
    └── userStatus.ts         ← UserOnline/Offline → ApiUpdate
```

## License obligation

Telegram-tt is **GPL-3.0**. Our fork inherits that. The full source —
including this adapter — must remain publicly available. See repo
root `LICENSE.GPL-3.0`.

The vendored WildFireChat SDK at `/wfc-sdk/` retains its original
WildFireChat license (already permissive — Apache-2.0-style).
