# Star Fire IM (星火 IM) — Architecture

## High-level

Star Fire IM is a self-hosted IM platform combining:

- **Modern UI** based on a fork of [Ajaxy/telegram-tt](https://github.com/Ajaxy/telegram-tt) (Telegram Web A, GPL-3.0)
- **WildFireChat IM protocol** (vendored JS SDK + existing Java app server) as the message transport, identity, and storage
- **Custom admin panel** (existing) for user review, content moderation, etc.

This is intentionally **not** a Telegram client — it does not speak MTProto and is not compatible with Telegram's network. It only borrows Telegram-tt's UI design language and React/Teact component tree.

## Repository layout

```
spark-im/
├── ARCHITECTURE.md              ← you are here
├── LICENSE.GPL-3.0              ← inherited from telegram-tt fork
├── README.md
├── web/                         ← Web frontend (telegram-tt fork + WildFireChat adapter)
│   ├── src/
│   │   ├── api/
│   │   │   ├── types/           ← API contract consumed by UI (Telegram-style ApiUser, ApiChat, ApiMessage)
│   │   │   └── wfc/             ← THIN adapter: maps callApi('X') → wfc.X() of the WildFireChat SDK
│   │   ├── components/          ← React/Teact UI (untouched from telegram-tt fork; brand strings rewritten)
│   │   ├── global/              ← App state (actions, selectors, reducers)
│   │   └── lib/                 ← Teact runtime, helpers
│   ├── public/                  ← favicons / manifest / static assets (rebranded for "星火 IM")
│   └── package.json
├── wfc-sdk/                     ← Vendored WildFireChat JS SDK (~11k lines, formerly web-chat/src/wfc/)
│   ├── sdk/                     ← The SDK proper (client, model, proto, av, ptt, util)
│   ├── custom-messages/         ← spark-im-specific custom message content types
│   ├── wfcScheme.js             ← URL scheme handler
│   └── wfc.css                  ← legacy CSS (may be stripped)
├── app-server/                  ← Java backend (Wildfire-compatible) — unchanged
├── admin-panel/                 ← Vue3 admin (user review, moderation) — unchanged
├── android-chat/                ← Android client (Wildfire) — separate roadmap
├── ios-chat/                    ← iOS client (Wildfire) — separate roadmap
├── landing-page/                ← marketing site
├── server/                      ← Wildfire IM server (binary build)
└── scripts/                     ← server-deploy.sh, telegram-tt-rebrand/, etc.
```

## What was removed

- `web-chat/` — the old Vue3 web frontend has been **deleted entirely**. Its `src/wfc/` (the WildFireChat SDK vendored copy) was preserved as `wfc-sdk/`; everything else (UI, components, routes, theme) is gone.
- `web/src/api/gramjs/` — the MTProto API adapter (12k+ lines) was removed.
- `web/src/lib/gramjs/` — the MTProto client library (110 files, 2.2 MB) was removed.

There are intentionally no parallel/duplicate UI codebases.

## Build & deploy

The new web frontend lives under `web/` and uses webpack (inherited from telegram-tt). Local dev:

```bash
cd web
npm install
npm run dev          # webpack-dev-server on :1234
```

Production build (server-side):

```bash
cd web
npm run build:production
rsync -a dist/ /opt/spark-im/web-chat/   # nginx doc root for chat.telvoro.top
```

The admin panel and app server build/deploy unchanged.

## Migration roadmap

| Phase | Status | Description |
|-------|--------|-------------|
| 0.5   | ✅ this PR | Tear down `web-chat/`, extract WildFireChat SDK to `wfc-sdk/`, fork telegram-tt into `web/`, remove gramjs MTProto adapter, stub out `web/src/api/wfc/` |
| 1     | TODO   | Wire login (账号 + 密码) → `wfc.connect()`. Mount on `chat-v2.telvoro.top`. Brand to "星火 IM". |
| 2     | TODO   | Conversation list + text message send/receive. Online status. |
| 3     | TODO   | Group chats + media upload (image/file/voice). |
| 4     | TODO   | Profile pages, settings, registration with admin review flow. |
| 5     | TODO   | Production cutover: chat-v2 → chat. Decommission old static dist. |

Telegram-only features that **will be hidden / removed** because they have no WildFireChat equivalent: Channels, Stickers (.tgs), Stars/TON/Premium, Stories, Secret Chats, Saved Messages, Scheduled Messages, Boosts/Giveaways, Bots inline mode.

See `web/src/api/wfc/README.md` for the API mapping table.

## Licenses

- `web/` — **GPL-3.0** (inherited from Ajaxy/telegram-tt)
- `wfc-sdk/` — **Apache-2.0-style** (WildFireChat SDK, vendored)
- `app-server/`, `admin-panel/`, `android-chat/`, `ios-chat/` — see respective `LICENSE` files

The combined repo is distributed under GPL-3.0 (the strongest license in the dependency graph). Source must remain publicly available.
