# Telegram-tt rebrand (Telegram Web A → 星火 IM)

This directory holds the assets and scripts used to compile the
[Ajaxy/telegram-tt](https://github.com/Ajaxy/telegram-tt) fork
(Telegram Web A) with spark-im branding so it can be embedded inside
spark-im (`chat.telvoro.top/telegram/`).

## Files

- `tt-rebrand.py`  — patches manifests, .env, webpack default title and `src/config.ts`
- `tt-rebrand2.py` — patches `src/index.html` (noscript fallback + meta description)
- `icons/`         — pre-rendered spark-im logo at every size telegram-tt expects
- `build-and-deploy.sh` — orchestrates clone → rebrand → install → build → deploy

## Usage

On the production server (where /opt/spark-im/source is the spark-im checkout):

```bash
export TELEGRAM_API_ID=YOUR_ID
export TELEGRAM_API_HASH=YOUR_HASH
sudo -E bash /opt/spark-im/source/scripts/telegram-tt-rebrand/build-and-deploy.sh
```

## What the rebrand changes

| Field                              | Before                                             | After          |
| ---------------------------------- | -------------------------------------------------- | -------------- |
| `<title>`                          | Telegram Web                                       | 星火 IM        |
| `manifest.name` / `short_name`     | Telegram Web                                       | 星火 IM        |
| `manifest.description`             | Telegram is a cloud-based mobile/desktop messaging | 星火 IM · 安全 · 高效 · 即时通讯 |
| `manifest.theme_color`             | #ffffff                                            | #3b82f6        |
| `manifest.background_color`        | #ffffff                                            | #0c1e4a        |
| `<noscript><h1>` fallback          | Telegram Web                                       | 星火 IM        |
| favicon / icon-192 / icon-512 etc. | Telegram airplane                                  | spark-im 星空火焰 logo |

## What is *not* rebranded (intentional)

- The QR center logo on the login screen — that is delivered by Telegram's MTProto auth API and embedded inside the QR image; replacing it would require modifying the protocol response.
- "Telegram" word inside chat-list / settings strings — these are user-visible product/protocol nouns we leave alone since the underlying service IS Telegram.
- Localizations (i18n keys) — not touched; we only rebrand the chrome.

## License notes

- **Ajaxy/telegram-tt** is GPL-3.0; redistributing a modified build means we must publish corresponding source. The patches in this directory + the upstream repo together satisfy that requirement.
- spark-im logo SVG (`/web-chat/public/icon.svg`) — owned by this project, used only here.
