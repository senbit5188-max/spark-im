<template>
    <div class="login-page-wrapper">
        <div class="login-bg-decoration">
            <div class="bg-orb bg-orb-1"></div>
            <div class="bg-orb bg-orb-2"></div>
            <div class="bg-orb bg-orb-3"></div>
        </div>
        <div class="login-container" :class="{'web-login-container': !sharedMiscState.isElectron}">
            <ElectronWindowsControlButtonView style="position: absolute; top: 0; right: 0"
                                              :maximizable="false"
                                              v-if="sharedMiscState.isElectronWindowsOrLinux"/>

            <div class="drag-area"/>
            <div v-if="loginType === 0" class="qrcode-login-container">
                <div class="qr-container" @click="regenerateQrCode">
                    <p v-if="qrCode === 'error'">生成二维码失败，点击重试<br>开发者请打开控制台查看日志</p>
                    <img v-else-if="qrCode" v-bind:src="qrCode" alt="">
                    <p v-else>{{ $t('misc.gen_qr_code') }}</p>
                    <ClipLoader v-if="loginStatus === 4" class="loading" :color="'white'" :height="'80px'" :width="'80px'"/>
                </div>
                <!--    等待扫码-->
                <div v-if="loginStatus === 0" class="pending-scan">
                    <p style="font-size: 20px; color: var(--text-primary); padding-bottom: 10px">{{ $t('login.desc') }}</p>
                    <p style="font-size: 15px; color: var(--text-secondary)">{{ $t('login.tip_web') }}</p>
                    <p style="font-size: 15px; color: var(--text-secondary); padding-bottom: 5px">{{ $t('login.warning') }}</p>
                    <a style="font-size: 15px; color: var(--accent-color)" target="_blank" href="https://telvoro.top">点击下载星火移动端</a>
                </div>
                <!--    已经扫码-->
                <div v-else-if="loginStatus === 1" class="scanned">
                    <p>{{ userName + $t('login.scan_qr_success') }}</p>
                    <p>{{ $t('login.confirm_login_tip') }}</p>
                    <label style="display: none">
                        {{ $t('login.remember_me') }}
                        <input type="checkbox" v-model="enableAutoLogin">
                    </label>
                    <button @click="cancel" class="button-cancel">{{ $t('login.cancel_login') }}</button>
                </div>

                <!--    存在session，等待发送给客户端验证-->
                <div v-if="loginStatus === 2" class="pending-quick-login">
                    <button @click="sendQuickLoginRequest" class="button-confirm">{{ $t('login.login') }}</button>
                    <button @click="cancel" class="button-cancel">{{ $t('login.switch_user') }}</button>
                </div>

                <!--    已经发送登录请求-->
                <div v-else-if="loginStatus === 3" class="quick-logining">
                    <p>{{ $t('login.confirm_login_tip') }}</p>
                    <button @click="cancel" class="button-cancel">{{ $t('login.cancel_login') }}</button>
                </div>

                <!--      开发调试时，自动登录-->
                <div v-else-if="loginStatus === 4">
                    <p>数据同步中，可能需要数分钟...</p>
                </div>
            </div>
            <div v-else-if="loginType === 1" class="login-form-container">
                <!--            密码登录-->
                <div class="login-brand">
                    <img class="logo" :src="require(`@/assets/images/icon.png`)" alt="">
                    <h1 class="title">星火 IM</h1>
                    <p class="subtitle">安全 · 高效 · 即时通讯</p>
                </div>
                <div class="login-fields">
                    <div class="field-group">
                        <label class="field-label">账号</label>
                        <div class="input-wrapper">
                            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/></svg>
                            <input v-model.trim="mobile" class="text-input" type="text" placeholder="请输入您的账号">
                        </div>
                    </div>
                    <div class="field-group">
                        <label class="field-label">密码</label>
                        <div class="input-wrapper">
                            <svg class="input-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5"><rect x="3" y="11" width="18" height="11" rx="2" ry="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/></svg>
                            <input v-model.trim="password" class="text-input" @keydown.enter="loginWithPassword" :type="showPassword ? 'text' : 'password'" placeholder="请输入您的密码">
                            <svg class="toggle-password" @click="showPassword = !showPassword" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.5">
                                <path v-if="!showPassword" d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/>
                                <circle v-if="!showPassword" cx="12" cy="12" r="3"/>
                                <path v-if="showPassword" d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24"/>
                                <line v-if="showPassword" x1="1" y1="1" x2="23" y2="23"/>
                            </svg>
                        </div>
                    </div>
                    <div class="remember-row">
                        <label class="remember-label">
                            <input type="checkbox" v-model="rememberPassword" class="remember-checkbox">
                            <span>记住密码</span>
                        </label>
                    </div>
                    <button class="login-button" :disabled="mobile === '' || !password || password === ''" ref="loginWithPasswordButton" @click="loginWithPassword">
                        <span v-if="loginStatus !== 3">登 录</span>
                        <span v-else class="login-loading">
                            <svg class="spin-icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M12 2v4M12 18v4M4.93 4.93l2.83 2.83M16.24 16.24l2.83 2.83M2 12h4M18 12h4M4.93 19.07l2.83-2.83M16.24 7.76l2.83-2.83"/></svg>
                            数据同步中...
                        </span>
                    </button>
                </div>
                <ClipLoader v-if="loginStatus === 3" class="syncing" :color="'var(--accent-color)'" :height="'80px'" :width="'80px'"/>
            </div>
            <div v-else class="login-form-container">
                <!--            验证码登录-->
                <img class="logo" :src="require(`@/assets/images/icon.png`)" alt="">
                <p class="title">验证码登录</p>
                <div class="item">
                    <input v-model.trim="mobile" class="text-input" type="number" placeholder="请输入手机号">
                </div>
                <div class="item">
                    <input v-model.trim="authCode" class="text-input" type="number" placeholder="验证码">
                    <button :disabled="mobile.toString().length !== 11 || authCodeCountdown > 0" class="request-auth-code-button" @keydown.enter="loginWithAuthCode" @click="requestAuthCode">{{ authCodeCountdown > 0 ? authCodeCountdown + 's后重新获取' : '获取验证码' }}</button>
                </div>
                <p v-if="loginStatus === 0" class="tip" @click="switchLoginType(1)">使用密码登录</p>
                <button class="login-button" :disabled="mobile === '' || authCode === ''" ref="loginWithAuthCodeButton" @click="loginWithAuthCode">{{ loginStatus === 3 ? '数据同步中，可能需要数分钟...' : '登录' }}</button>
                <ClipLoader v-if="loginStatus === 3" style="margin-top: 10px" class="syncing" :color="'4168e0'" :height="'80px'" :width="'80px'"/>
            </div>

            <p class="diagnose" style="display:none" @click="diagnose">诊断</p>
        </div>

        <div v-if="showDiagnoseOverlay" class="diagnose-overlay">
            <div class="diagnose-content">
                <pre>{{ diagnoseResult }}</pre>
                <button @click="closeDiagnoseOverlay">关闭</button>
            </div>
        </div>
        <!-- 滑动验证对话框 -->
        <SlideVerifyDialog
            v-if="enableLoginSlideVerify"
            ref="slideVerifyDialog"
            @verify-success="onSlideVerifySuccess"
            @verify-failed="onSlideVerifyFailed"
            @load-failed="onSlideVerifyLoadFailed"
        />
    </div>
</template>

<script>
import Config from "../../config";
import wfc from '../../wfc/client/wfc'
import PCSession from "../../wfc/model/pcsession";
import jrQRCode from 'jr-qrcode'
import ClipLoader from 'vue-spinner/src/ClipLoader'
import ConnectionStatus from "../../wfc/client/connectionStatus";
import EventType from "../../wfc/client/wfcEvent";
import {clear, getItem, setItem} from "../util/storageHelper";
import {ipcRenderer, isElectron} from "../../platform";
import store from "../../store";
import ElectronWindowsControlButtonView from "../common/ElectronWindowsControlButtonView.vue";
import IpcEventType from "../../ipcEventType";
import appServerApi from "../../api/appServerApi";
import organizationServerApi from "../../api/organizationServerApi";
import WfcScheme from "../../wfcScheme";
import axios from "axios";
import avenginekit from "../../wfc/av/internal/engine.min";
import SlideVerifyDialog from "../common/SlideVerifyDialog.vue";

export default {
    name: 'LoginPage',
    data() {
        return {
            sharedMiscState: store.state.misc,
            qrCode: '',
            userName: '',
            loginStatus: 0, //0 等待扫码，密码登录或验证码登录时，表示等待登录； 1 已经扫码； 2 存在session，等待发送给客户端验证；3 已经发送登录请求，密码登录或验证码登录时，表示登录中 4 调试时，自动登录
            qrCodeTimer: null,
            appToken: '',
            lastAppToken: '',
            loginType: 1, // 0 扫码登录，1 密码登录，2 验证码登录
            enableAutoLogin: Config.ENABLE_AUTO_LOGIN,
            mobile: '',
            password: '',
            showPassword: false,
            rememberPassword: false,
            authCode: '',
            firstTimeConnect: false,
            diagnoseResult: '',
            showDiagnoseOverlay: false,
            enableLoginSlideVerify: Config.ENABLE_LOGIN_SLIDE_VERIFY,
            // 滑动验证相关
            hasSlideVerifiedForCode: false, // 是否已通过滑动验证（用于验证码登录）
            cachedSlideVerifyToken: null,  // 缓存的验证token
            pendingLoginAction: null,      // 待执行的登录操作
            authCodeCountdown: 0,          // 获取验证码倒计时
            authCodeTimer: null,           // 倒计时定时器
        }
    },
    created() {
        wfc.eventEmitter.on(EventType.ConnectionStatusChanged, this.onConnectionStatusChange)

        // Restore remembered credentials
        let savedMobile = getItem('rememberedMobile');
        let savedPassword = getItem('rememberedPassword');
        if (savedMobile && savedPassword) {
            this.mobile = savedMobile;
            this.password = savedPassword;
            this.rememberPassword = true;
        }

        let userId = getItem('userId');
        let token = getItem('token');
        if (userId) {
            let portrait = getItem("userPortrait");
            this.qrCode = portrait ? portrait : Config.DEFAULT_PORTRAIT_URL;

            let autoLogin = getItem(userId + '-' + 'autoLogin') === '1'
            if (autoLogin && token) {
                console.log('autoLogin', userId)
                this.firstTimeConnect = wfc.connect(userId, token);
                this.loginStatus = 4;
            } else {
                this.loginStatus = 2;
                isElectron() && ipcRenderer.send(IpcEventType.RESIZE_LOGIN_WINDOW);
            }
        } else {
            isElectron() && ipcRenderer.send(IpcEventType.RESIZE_LOGIN_WINDOW);
            this.refreshQrCode();
        }
    },

    beforeUnmount() {
        wfc.eventEmitter.removeListener(EventType.ConnectionStatusChanged, this.onConnectionStatusChange)
    },

    methods: {
        register() {
            this.$notify({
                text: '使用短信验证码登录，将会为您创建账户，请使用短信验证码登录',
                type: 'info'
            });
            this.switchLoginType(2);
        },
        switchLoginType(type) {
            this.loginType = type;
            if (this.loginType === 0) {
                this.refreshQrCode();
            } else {
                if (this.qrCodeTimer) {
                    clearInterval(this.qrCodeTimer);
                    this.qrCodeTimer = 0;
                }

                // 切换登录模式时，重置验证标志
                this.hasSlideVerifiedForCode = false;
                this.cachedSlideVerifyToken = null;
            }
        },

        startAuthCodeCountdown() {
            this.authCodeCountdown = 60;
            if (this.authCodeTimer) {
                clearInterval(this.authCodeTimer);
            }
            this.authCodeTimer = setInterval(() => {
                this.authCodeCountdown--;
                if (this.authCodeCountdown <= 0) {
                    clearInterval(this.authCodeTimer);
                    this.authCodeTimer = null;
                }
            }, 1000);
        },
        async requestAuthCode() {
            this.pendingLoginAction = () => {
                appServerApi.requestAuthCode(this.mobile, this.cachedSlideVerifyToken)
                .then(response => {
                    this.$notify({
                        text: '发送验证码成功',
                        type: 'info'
                    });
                        // 标记已通过滑动验证
                        this.hasSlideVerifiedForCode = true;
                        this.startAuthCodeCountdown();
                })
                .catch(err => {
                        // 发送失败，重置验证标志
                        this.hasSlideVerifiedForCode = false;
                        this.cachedSlideVerifyToken = null;
                    this.$notify({
                        title: '发送验证码失败',
                        text: err.message,
                        type: 'error'
                    });
                })
            };
            if (this.enableLoginSlideVerify) {
                this.$refs.slideVerifyDialog.show();
            } else {
                this.pendingLoginAction();
                this.pendingLoginAction = null;
            }
        },

        async loginWithPassword() {
            if (!this.mobile || !this.password) {
                return;
            }

            // 特殊用途，请勿打开
            // 必须在 getClientId 之前调用，createPCLoginSession 会触发调用 getClientId，打开时，需重新设计起逻辑
            // wfc.setAppName('wfc-' + this.mobile);
            this.pendingLoginAction = () => {
            this.$refs.loginWithPasswordButton.disabled = true;
            this.loginStatus = 3;
                appServerApi.loinWithPassword(this.mobile, this.password, this.cachedSlideVerifyToken)
                .then(res => {
                    const {userId, token, portrait} = res
                    this.firstTimeConnect = wfc.connect(userId, token);
                    setItem('userId', userId);
                    setItem('token', token);
                    setItem("userPortrait", portrait);
                    // Save or clear remembered credentials
                    if (this.rememberPassword) {
                        setItem('rememberedMobile', this.mobile);
                        setItem('rememberedPassword', this.password);
                    } else {
                        setItem('rememberedMobile', '');
                        setItem('rememberedPassword', '');
                    }
                })
                .catch(err => {
                    console.log('loginWithPassword err', err)
                        this.$refs.loginWithPasswordButton.disabled = false;
                    this.password = '';
                    this.loginStatus = 0;
                        // 登录失败，重置验证标志
                        this.hasSlideVerifiedForCode = false;
                        this.cachedSlideVerifyToken = null;
                    this.$notify({
                        title: '登录失败',
                        text: err.message,
                        type: 'error'
                    });
                })
            };
            if (this.enableLoginSlideVerify) {
                this.$refs.slideVerifyDialog.show();
            } else {
                this.pendingLoginAction();
                this.pendingLoginAction = null;
            }
        },

        async loginWithAuthCode() {
            if (!this.mobile || !this.authCode) {
                return;
            }

            if (!this.enableLoginSlideVerify) {
                this.cachedSlideVerifyToken = null;
                this.performAuthCodeLogin();
                return;
            }
            // 如果已经通过滑动验证（发送验证码时已验证），直接登录
            if (this.hasSlideVerifiedForCode && this.cachedSlideVerifyToken) {
                this.performAuthCodeLogin();
                return;
            }

            // 显示滑动验证
            this.$refs.slideVerifyDialog.show();
            this.pendingLoginAction = () => {
                this.performAuthCodeLogin();
            };
        },

        performAuthCodeLogin() {
            this.$refs.loginWithAuthCodeButton.disabled = true;
            this.loginStatus = 3;
            appServerApi.loginWithAuthCode(this.mobile, this.authCode, this.cachedSlideVerifyToken)
                .then(res => {
                    const {userId, token, portrait} = res;
                    this.firstTimeConnect = wfc.connect(userId, token);
                    setItem('userId', userId);
                    setItem('token', token);
                    setItem("userPortrait", portrait);
                })
                .catch(err => {
                    this.$refs.loginWithAuthCodeButton.disabled = false;
                    this.authCode = '';
                    this.loginStatus = 0;
                    // 登录失败，重置验证标志
                    this.hasSlideVerifiedForCode = false;
                    this.cachedSlideVerifyToken = null;
                    this.$notify({
                        title: '登录失败',
                        text: err.message,
                        type: 'error'
                    });
                })
        },

        regenerateQrCode() {
            if (this.qrCode === 'error') {
                this.qrCode = null;
                this.createPCLoginSession(null);
            }
        },
        async createPCLoginSession(userId) {
            //wfc.setAppName('wfc-' + this.mobile);
            appServerApi.createPCSession(userId)
                .then(response => {
                    let session = Object.assign(new PCSession(), response);
                    this.appToken = session.token;
                    if (!userId || session.status === 0/*服务端pc login session不存在*/) {
                        this.qrCode = jrQRCode.getQrBase64(WfcScheme.QR_CODE_PREFIX_PC_SESSION + session.token);
                    }
                    this.login();
                })
                .catch(err => {
                    console.log('createPCSession error', err);
                    console.log('!!! 如果是 CORS 相关错误，可能是通过 nginx 等代理了 app-server 相关请求，但 nginx 配置错误，请参考 app-server 项目下 nginx 目录下的参考配置 !!!')
                    this.qrCode = 'error';
                    if (this.qrCodeTimer) {
                        clearInterval(this.qrCodeTimer)
                    }
                })
        },

        async refreshQrCode() {
            await this.createPCLoginSession(null);
            if (!this.qrCodeTimer) {
                this.qrCodeTimer = setInterval(() => {
                    if (this.loginStatus === 3) {
                        return;
                    }
                    this.appToken = '';
                    this.loginStatus = 0;
                    this.createPCLoginSession(null);
                }, 60 * 1000);
            }
        },

        async login() {
            this.lastAppToken = this.appToken;
            appServerApi.loginWithPCSession(this.appToken)
                .then(data => {
                    if (data) {
                        switch (data.code) {
                            case 0:
                                if (this.loginStatus === 1 || this.loginStatus === 3) {
                                    let userId = data.result.userId;
                                    let imToken = data.result.token;
                                    wfc.connect(userId, imToken);
                                    this.loginStatus = 4;
                                    setItem('userId', userId);
                                    setItem('token', imToken);
                                }
                                break;
                            case 9:
                                if (data.result.portrait) {
                                    this.qrCode = data.result.portrait;
                                } else {
                                    this.qrCode = Config.DEFAULT_PORTRAIT_URL;
                                }
                                setItem("userName", data.result.userName);
                                setItem("userPortrait", data.result.portrait);

                                if (this.loginStatus === 0) {
                                    this.loginStatus = 1;
                                } else {
                                    this.loginStatus = 3;
                                }
                                this.login();
                                break;
                            case 18:
                                //session is canceled, need clear last time login status
                                this.cancel();
                                break;
                            default:
                                this.lastAppToken = '';
                                console.log(data);
                                break
                        }
                    }
                })
                .catch(err => {
                });
        },

        sendQuickLoginRequest() {
            let userId = getItem("userId");
            this.createPCLoginSession(userId);
            this.loginStatus = 3;
        },

        cancel() {

            this.loginStatus = 0;
            this.qrCode = null;
            // 切换用户时，先进行disconnect
            wfc.disconnect();
            clear();

            // 重置滑动验证状态
            this.hasSlideVerifiedForCode = false;
            this.cachedSlideVerifyToken = null;
            this.pendingLoginAction = null;
            this.refreshQrCode();
        },

        onConnectionStatusChange(status) {
            if (status === ConnectionStatus.ConnectionStatusLogout
                || status === ConnectionStatus.ConnectionStatusRejected
                || status === ConnectionStatus.ConnectionStatusSecretKeyMismatch
                || status === ConnectionStatus.ConnectionStatusKickedOff
                || status === ConnectionStatus.ConnectionStatusNotLicensed
                || status === ConnectionStatus.ConnectionStatusTimeInconsistent
                || status === ConnectionStatus.ConnectionStatusServerDown
                || status === ConnectionStatus.ConnectionStatusUnconnected
                || status === ConnectionStatus.ConnectionStatusTokenIncorrect) {
                this.password = '';
                this.authCode = '';
                this.loginStatus = 0;
                if (this.loginType === 0) {
                    this.refreshQrCode();
                }
                if (status !== ConnectionStatus.ConnectionStatusLogout && status !== ConnectionStatus.ConnectionStatusUnconnected) {
                    console.error('连接失败，请根据下面描述进行排查', status)
                     console.error(ConnectionStatus.desc(status))
                    this.cancel();
                    this.diagnose();
                    this.$notify({
                        text: '连接失败，请打开控制台，查看具体日志',
                        type: 'error'
                    });
                }
            }
            if (status === ConnectionStatus.ConnectionStatusReceiveing) {
                if (this.$refs.loginWithAuthCodeButton) {
                    this.$refs.loginWithAuthCodeButton.textContent = '数据同步中，可能需要数分钟...';
                }
                if (this.$refs.loginWithPasswordButton) {
                    this.$refs.loginWithPasswordButton.textContent = '数据同步中，可能需要数分钟...';
                }
            }

            if (status === ConnectionStatus.ConnectionStatusConnected) {
                if (isElectron()) {
                    ipcRenderer.send(IpcEventType.LOGIN, {closeWindowToExit: getItem(wfc.getUserId() + '-' + 'closeWindowToExit') === '1'})
                }
                this.$router.replace({path: "/home"});
                if (isElectron() || (Config.CLIENT_ID_STRATEGY === 1 || Config.CLIENT_ID_STRATEGY === 2)) {
                    isElectron() && ipcRenderer.send(IpcEventType.LOGIN, {userId: wfc.getUserId(), closeWindowToExit: getItem(wfc.getUserId() + '-' + 'closeWindowToExit') === '1'})
                    if (this.enableAutoLogin) {
                        store.setEnableAutoLogin(this.enableAutoLogin)
                    }
                }
                organizationServerApi.login()
                    .catch(r => {
                        console.error('organizationServer login failed', r)
                    });
            }
        },

        async diagnose() {
            // TODO
            // app-server
            // api/version
            // tcp ping
            console.log('diagnose...')

            let configInfo = '';
            let routeHost = wfc.getHost()
            let routePort = Config.ROUTE_PORT
            let useWss = Config.USE_WSS
            configInfo += `APP-Server: ${Config.APP_SERVER}\n`
            configInfo += `IM-Server-Host: ${routeHost}\n`
            configInfo += `USE_WSS: ${useWss}\n`
            configInfo += `ROUTE_PORT: ${routePort}\n`

            configInfo += `Web SDK: ${wfc.getVersion()}\n`
            configInfo += `音视频 SDK: ${avenginekit.startConference !== undefined ? '高级版' : '多人版'}`
            configInfo += '\n'

            let ices = '';
            if (Config.ICE_SERVERS && Config.ICE_SERVERS.length > 0) {
                ices = Config.ICE_SERVERS[0][0] + ' ' + Config.ICE_SERVERS[0][1] + ' ' + Config.ICE_SERVERS[0][2]
            }
            configInfo += `Turn-Server: ${ices}\n`



            if (Config.APP_SERVER.startsWith('https:') && !Config.USE_WSS) {
                configInfo += 'USE_WSS 配置错误：APP-Server 使用 https，但没有启用 wss，请修改 Config.USE_WSS = true\n'
            }
            if (Config.APP_SERVER.startsWith('http:') && Config.USE_WSS) {
                configInfo += 'USE_WSS 配置错误：APP-Server 使用 http，但是启用了 wss，请修改 Config.USE_WSS = false\n'
            }

            if (Config.APP_SERVER.startsWith('https:') && Config.ROUTE_PORT !== 443) {
                configInfo += '警告：APP-Server 使用 https，但 ROUTE_PORT 非标准 443 端口\n'
            }
            if (Config.APP_SERVER.startsWith('http:') && Config.ROUTE_PORT !== 80) {
                configInfo += '警告：APP-Server 使用 http，但 ROUTE_PORT 非标准 80 端口\n'
            }

            console.warn('-----configInfo start---------\n')
            console.warn(configInfo);
            console.warn('-----configInfo end---------\n')

            let result = '';
            let appServerResponse = await axios.get(Config.APP_SERVER, {
                transformResponse: [data => data],
            })
            if (appServerResponse.data === 'Ok') {
                result += 'APP-Server 正常\n';
            } else {
                result += 'APP-Server 异常: ' + appServerResponse.status + '\n';
            }
            if (routeHost) {
                let url = `${useWss ? 'https://' : 'http://'}${routeHost}:${routePort}/api/version`
                try {
                    let apiVersion = await axios.get(url)
                    result += 'IM-Server api/version 正常\n'
                    result += `remoteOriginUrl: ${apiVersion.data.remoteOriginUrl}\n`
                    result += `commitId: ${apiVersion.data.commitId}\n`
                    result += `commitTime: ${apiVersion.data.commitTime}\n`
                    result += `buildTime: ${apiVersion.data.buildTime}\n`
                } catch (e) {
                    result += `IM-Server api/version 异常：${e}\n`
                }
            } else {
                result += 'IM-Server 未知：未执行connect 操作'
            }


            console.log('result', result);

            this.diagnoseResult = configInfo + '\n' + result;
            this.showDiagnoseOverlay = true
        },
        closeDiagnoseOverlay() {
            this.showDiagnoseOverlay = false;
        },

        // 滑动验证事件处理
        onSlideVerifySuccess(token) {
            this.cachedSlideVerifyToken = token;
            if (this.pendingLoginAction) {
                this.pendingLoginAction();
                this.pendingLoginAction = null;
            }
        },

        onSlideVerifyFailed() {
            // 验证失败，不关闭窗口，让用户重试
        },

        onSlideVerifyLoadFailed() {
            // 加载验证码失败
            this.cachedSlideVerifyToken = null;
            this.pendingLoginAction = null;
            this.$notify({
                title: '加载验证码失败',
                text: '请稍后重试',
                type: 'error'
            });
        }
    },

    computed: {
        pStyle() {
            if (isElectron()) {
                return {
                    color: 'white',
                    padding: '5px',
                }
            } else {
                return {
                    padding: '5px',
                }
            }
        }
    },

    unmounted() {
        if (this.qrCodeTimer) {
            clearInterval(this.qrCodeTimer)
        }
    },

    components: {
        ElectronWindowsControlButtonView,
        ClipLoader,
        SlideVerifyDialog,
    }

}
</script>

<style lang="css" scoped>
.login-page-wrapper {
    position: relative;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background: linear-gradient(145deg, #e8f7ee 0%, #f3faf6 30%, #ffffff 60%, #ecf8f1 100%);
    overflow: hidden;
}

.login-bg-decoration {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    overflow: hidden;
}

.bg-orb {
    position: absolute;
    border-radius: 50%;
    filter: blur(80px);
}

.bg-orb-1 {
    width: 500px;
    height: 500px;
    background: radial-gradient(circle, rgba(7, 193, 96, 0.14) 0%, transparent 70%);
    top: -150px;
    right: -100px;
    animation: float-orb 20s ease-in-out infinite;
}

.bg-orb-2 {
    width: 400px;
    height: 400px;
    background: radial-gradient(circle, rgba(7, 193, 96, 0.08) 0%, transparent 70%);
    bottom: -100px;
    left: -100px;
    animation: float-orb 25s ease-in-out infinite reverse;
}

.bg-orb-3 {
    width: 300px;
    height: 300px;
    background: radial-gradient(circle, rgba(26, 173, 25, 0.06) 0%, transparent 70%);
    top: 40%;
    left: 60%;
    animation: float-orb 18s ease-in-out infinite;
    animation-delay: -5s;
}

@keyframes float-orb {
    0%, 100% { transform: translate(0, 0) scale(1); }
    33% { transform: translate(20px, -30px) scale(1.05); }
    66% { transform: translate(-15px, 20px) scale(0.95); }
}

.login-container {
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    width: 440px;
    height: auto;
    min-height: 540px;
    padding: 48px 40px;
    margin: auto;
    background: rgba(255, 255, 255, 0.85);
    backdrop-filter: blur(24px);
    -webkit-backdrop-filter: blur(24px);
    border-radius: 24px;
    border: 1px solid rgba(255, 255, 255, 0.6);
}

.web-login-container {
    box-shadow: 0 24px 80px rgba(0, 0, 0, 0.08),
                0 8px 24px rgba(0, 0, 0, 0.04),
                0 0 0 1px rgba(0, 0, 0, 0.02);
}

.qr-container {
    border-radius: 12px;
    width: 220px;
    height: 220px;
    background-color: #f5f7fa;
    display: flex;
    justify-content: center;
    align-items: center;
    font-size: 14px;
}

.qr-container img {
    width: 220px;
    height: 220px;
    border-radius: 12px;
    object-fit: cover;
}

.qr-container .loading {
    position: absolute;
    border-width: 4px;
}

.pending-scan,
.scanned,
.pending-quick-login,
.quick-logining {
    display: flex;
    margin-top: 20px;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    line-height: 25px;
}

.qrcode-login-container {
    margin-top: 20px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
}

.qrcode-login-container label {
    margin-top: 5px;
    padding: 5px;
    font-size: 14px;
    color: #8c8c8c;
}

.qrcode-login-container button {
    outline: none;
    font-size: 14px;
    border: none;
    border-radius: 10px;
}

.button-cancel {
    margin-top: 10px;
    background-color: transparent;
    color: #8c8c8c;
}

.button-cancel:active,
.button-cancel:hover {
    color: #06ad56;
}

.button-confirm {
    width: 200px;
    height: 46px;
    color: #fff;
    background: linear-gradient(135deg, #07c160 0%, #1aad19 100%);
    border-radius: 12px;
}

.button-confirm:hover {
    background: linear-gradient(135deg, #06ad56 0%, #168a17 100%);
}

.drag-area {
    position: absolute;
    top: 0;
    left: 0;
    right: 150px;
    height: 60px;
    z-index: -1;
    -webkit-app-region: drag;
}

.switch-login-type-container {
    padding-top: 10px;
    font-size: 14px;
    color: #06ad56;
}

.login-form-container {
    width: 340px;
    display: flex;
    flex-direction: column;
    justify-content: center;
    align-items: center;
    position: relative;
}

.login-brand {
    display: flex;
    flex-direction: column;
    align-items: center;
    margin-bottom: 32px;
}

.login-brand .title {
    font-size: 28px;
    font-weight: 700;
    color: #0f172a;
    margin: 0;
    letter-spacing: 2px;
}

.login-brand .subtitle {
    font-size: 13px;
    color: #94a3b8;
    margin-top: 6px;
    letter-spacing: 4px;
    font-weight: 400;
}

.login-fields {
    width: 100%;
}

.field-group {
    margin-bottom: 20px;
}

.field-label {
    display: block;
    font-size: 13px;
    font-weight: 500;
    color: #475569;
    margin-bottom: 8px;
}

.input-wrapper {
    display: flex;
    align-items: center;
    border: 1.5px solid #e2e8f0;
    border-radius: 12px;
    background: rgba(248, 250, 252, 0.8);
    transition: all 0.25s cubic-bezier(0.4, 0, 0.2, 1);
    padding: 0 14px;
}

.input-wrapper:focus-within {
    border-color: #07c160;
    background: #fff;
    box-shadow: 0 0 0 4px rgba(7, 193, 96, 0.08);
}

.input-icon {
    width: 18px;
    height: 18px;
    color: #94a3b8;
    flex-shrink: 0;
    transition: color 0.25s;
}

.input-wrapper:focus-within .input-icon {
    color: #07c160;
}

.toggle-password {
    width: 20px;
    height: 20px;
    color: #94a3b8;
    cursor: pointer;
    flex-shrink: 0;
    transition: color 0.25s;
}

.toggle-password:hover {
    color: #07c160;
}

.login-form-container .text-input {
    height: 48px;
    width: 100%;
    border: none;
    outline: none;
    padding: 0 12px;
    font-size: 15px;
    color: #0f172a;
    background: transparent;
    -moz-appearance: textfield;
}

.login-form-container .text-input::placeholder {
    color: #cbd5e1;
}

input::-webkit-outer-spin-button,
input::-webkit-inner-spin-button {
    -webkit-appearance: none;
    margin: 0;
}

.remember-row {
    width: 100%;
    display: flex;
    justify-content: flex-start;
    margin-bottom: 4px;
}

.remember-label {
    display: flex;
    align-items: center;
    gap: 8px;
    font-size: 13px;
    color: #64748b;
    cursor: pointer;
    user-select: none;
}

.remember-checkbox {
    width: 16px;
    height: 16px;
    accent-color: #07c160;
    cursor: pointer;
    border-radius: 4px;
}

.login-form-container .login-button {
    height: 50px;
    width: 100%;
    margin-top: 16px;
    border: none;
    border-radius: 14px;
    background: linear-gradient(135deg, #07c160 0%, #1aad19 50%, #34c984 100%);
    color: #fff;
    font-size: 16px;
    font-weight: 600;
    letter-spacing: 6px;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    position: relative;
    overflow: hidden;
}

.login-form-container .login-button::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255,255,255,0.15), transparent);
    transition: left 0.5s;
}

.login-form-container .login-button:hover:not(:disabled)::before {
    left: 100%;
}

.login-form-container .login-button:hover:not(:disabled) {
    box-shadow: 0 8px 24px rgba(26, 109, 255, 0.3),
                0 4px 8px rgba(26, 109, 255, 0.15);
    transform: translateY(-2px);
}

.login-form-container .login-button:active:not(:disabled) {
    transform: translateY(0);
    box-shadow: 0 2px 8px rgba(26, 109, 255, 0.2);
}

.login-form-container .login-button:disabled {
    background: linear-gradient(135deg, #cbd5e1 0%, #e2e8f0 100%);
    cursor: not-allowed;
}

.login-loading {
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    letter-spacing: 2px;
}

.spin-icon {
    width: 18px;
    height: 18px;
    animation: spin 1s linear infinite;
}

@keyframes spin {
    from { transform: rotate(0deg); }
    to { transform: rotate(360deg); }
}

.login-form-container .request-auth-code-button {
    position: absolute;
    font-size: 12px;
    top: 50%;
    right: 0;
    transform: translateY(-50%);
    margin: 0 5px;
}

.login-form-container .syncing {
    position: absolute;
    bottom: 0;
    color: #07c160;
}

.tip {
    align-self: flex-start;
    font-size: 12px;
    color: #07c160;
    margin-top: 10px;
    cursor: pointer;
}

.tip:hover {
    text-decoration: underline;
}

.logo {
    width: 72px;
    height: 72px;
    margin-bottom: 16px;
    border-radius: 18px;
    filter: drop-shadow(0 4px 12px rgba(7, 193, 96, 0.25));
}

.diagnose {
    position: absolute;
    right: 10px;
    bottom: 10px;
    align-self: flex-start;
    font-size: 12px;
    color: #ccc;
}

.diagnose:hover {
    color: #999;
}

.diagnose-overlay {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    background: rgba(0,0,0,0.5);
    display: flex;
    justify-content: center;
    align-items: center;
    z-index: 1000;
}

.diagnose-content {
    background: #fff;
    padding: 24px;
    border-radius: 16px;
    max-width: 90%;
    max-height: 90%;
    overflow: auto;
    display: flex;
    flex-direction: column;
    align-items: center;
    box-shadow: 0 24px 80px rgba(0,0,0,0.15);
}

.diagnose-content pre {
    width: 100%;
    text-align: left;
}

.diagnose-content button {
    margin-top: 20px;
    padding: 8px 24px;
    border: none;
    border-radius: 10px;
    background: #07c160;
    color: #fff;
    cursor: pointer;
}

@media (max-width: 768px) {
    .login-page-wrapper {
        padding: 16px;
    }

    .login-container {
        width: 100%;
        max-width: 440px;
        height: auto;
        min-height: 440px;
        padding: 32px 24px;
        border-radius: 20px;
    }

    .logo {
        width: 56px;
        height: 56px;
    }

    .login-brand {
        margin-bottom: 24px;
    }

    .login-brand .title {
        font-size: 22px;
    }

    .login-form-container {
        width: 100%;
    }

    .qr-container {
        width: 180px;
        height: 180px;
    }

    .qr-container img {
        width: 180px;
        height: 180px;
    }

    .bg-orb {
        display: none;
    }
}
</style>
