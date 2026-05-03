<template>
    <section id="conversation-content" class="conversation-page" :class="{'has-active-conversation': hasActiveConversation}">
        <ConversationListPanel class="conversation-list-panel"/>
        <ConversationView class="conversation-view"/>
    </section>
</template>

<script>
import ConversationView from "./conversation/ConversationView";
import ConversationListPanel from "./ConversationListPanel.vue";
import store from "../../store";

export default {
    name: "ConversationPage",
    data() {
        return {
            sharedConversationState: store.state.conversation,
        };
    },
    computed: {
        hasActiveConversation() {
            return this.sharedConversationState.currentConversationInfo != null;
        }
    },
    unmounted() {
        console.log('conversation page destroyed')
    },

    methods: {},
    components: {
        ConversationListPanel,
        ConversationView,
    },
};
</script>

<style lang="css" scoped>
.conversation-page {
    flex: 1;
    display: flex;
    height: 100%;
}

.conversation-list-panel {
    width: 261px;
    height: 100%;
}

.conversation-view {
    flex: 1;
}

@media (max-width: 768px) {
    .conversation-list-panel {
        width: 100%;
    }

    .conversation-view {
        display: none;
    }

    .has-active-conversation .conversation-list-panel {
        display: none;
    }

    .has-active-conversation .conversation-view {
        display: block;
        width: 100%;
    }
}

</style>
