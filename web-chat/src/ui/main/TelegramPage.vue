<template>
    <div class="telegram-container">
        <iframe
            class="telegram-frame"
            src="/telegram/"
            referrerpolicy="origin"
            allow="clipboard-read; clipboard-write; camera; microphone; fullscreen; autoplay; picture-in-picture; display-capture"
            sandbox="allow-same-origin allow-scripts allow-forms allow-popups allow-popups-to-escape-sandbox allow-modals allow-downloads allow-storage-access-by-user-activation allow-presentation"
            title="Telegram Web"
        ></iframe>
    </div>
</template>

<script>
export default {
    name: "TelegramPage"
}
</script>

<style lang="css" scoped>
.telegram-container {
    width: 100%;
    height: 100%;
    background-color: var(--background-tertiary, #1f1f22);
    border-top-right-radius: var(--main-border-radius, 0);
    border-bottom-right-radius: var(--main-border-radius, 0);
    overflow: hidden;
    display: flex;
}

.telegram-frame {
    flex: 1;
    width: 100%;
    height: 100%;
    border: 0;
    background-color: #ffffff;
    border-top-right-radius: inherit;
    border-bottom-right-radius: inherit;
}
</style>
