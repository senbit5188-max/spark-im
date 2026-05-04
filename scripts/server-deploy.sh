#!/usr/bin/env bash
# In-server build & deploy for spark-im static frontends.
#
# Run this on the production server (telvoro.top), under /opt/spark-im/source/:
#   /opt/spark-im/source/scripts/server-deploy.sh                 # all (web-chat + landing-page + admin-panel)
#   /opt/spark-im/source/scripts/server-deploy.sh web-chat        # only web-chat
#   /opt/spark-im/source/scripts/server-deploy.sh landing-page    # only landing-page
#   /opt/spark-im/source/scripts/server-deploy.sh admin-panel     # only admin-panel
#   SKIP_PULL=1 .../server-deploy.sh                              # skip git pull
#   SKIP_BUILD=1 .../server-deploy.sh                             # skip npm install + build (re-deploy existing dist)
#   BRANCH=main .../server-deploy.sh                              # check out a different branch first

set -euo pipefail

SOURCE_DIR="/opt/spark-im/source"
DOC_ROOT_BASE="/opt/spark-im"
TARGET="${1:-all}"

if [[ ! -d "${SOURCE_DIR}/.git" ]]; then
    echo "ERROR: ${SOURCE_DIR} does not look like a git checkout." >&2
    exit 1
fi

cd "${SOURCE_DIR}"

if [[ -z "${SKIP_PULL:-}" ]]; then
    if [[ -n "${BRANCH:-}" ]]; then
        echo "==> git fetch + checkout ${BRANCH}"
        git fetch origin "${BRANCH}"
        git checkout "${BRANCH}"
    fi
    echo "==> git pull (current branch: $(git rev-parse --abbrev-ref HEAD))"
    git pull --ff-only
fi

deploy_web_chat() {
    echo "==> Building web-chat"
    cd "${SOURCE_DIR}/web-chat"
    if [[ -z "${SKIP_BUILD:-}" ]]; then
        npm install --no-audit --no-fund --prefer-offline
        NODE_ENV=production npm run build
    fi
    echo "==> Syncing web-chat dist -> ${DOC_ROOT_BASE}/web-chat"
    rsync -a --delete \
        --chmod=Du=rwx,Dgo=rx,Fu=rw,Fgo=r \
        "${SOURCE_DIR}/web-chat/dist/" \
        "${DOC_ROOT_BASE}/web-chat/"
}

deploy_landing_page() {
    echo "==> Syncing landing-page -> ${DOC_ROOT_BASE}/landing-page"
    rsync -a --delete \
        --chmod=Du=rwx,Dgo=rx,Fu=rw,Fgo=r \
        --exclude='*.md' \
        "${SOURCE_DIR}/landing-page/" \
        "${DOC_ROOT_BASE}/landing-page/"
}

deploy_admin_panel() {
    echo "==> Syncing admin-panel -> ${DOC_ROOT_BASE}/admin-panel"
    rsync -a --delete \
        --chmod=Du=rwx,Dgo=rx,Fu=rw,Fgo=r \
        --exclude='*.md' \
        "${SOURCE_DIR}/admin-panel/" \
        "${DOC_ROOT_BASE}/admin-panel/"
}

case "${TARGET}" in
    all)
        deploy_web_chat
        deploy_landing_page
        deploy_admin_panel
        ;;
    web-chat)     deploy_web_chat ;;
    landing-page) deploy_landing_page ;;
    admin-panel)  deploy_admin_panel ;;
    *)
        echo "ERROR: unknown target '${TARGET}' (expected: all | web-chat | landing-page | admin-panel)" >&2
        exit 1
        ;;
esac

echo "==> nginx -t && nginx -s reload"
nginx -t
systemctl reload nginx 2>/dev/null || nginx -s reload

echo "==> Smoke test"
for url in https://chat.telvoro.top https://telvoro.top https://admin.telvoro.top https://api.telvoro.top; do
    code=$(curl -s -o /dev/null -w '%{http_code}' "${url}")
    echo "  ${url} -> HTTP ${code}"
done

echo "==> Done."
