const path = require('path');
const webpack = require('webpack')
const CompressionPlugin = require('compression-webpack-plugin')

function resolve(dir) {
    return path.join(__dirname, dir);
}

const isProd = process.env.NODE_ENV === 'production'

module.exports = {
    devServer: {
        allowedHosts: 'all',
        port: 8013
    },
    publicPath: '/',
    outputDir: 'dist',
    //assetsDir: 'static',
    lintOnSave: process.env.NODE_ENV === 'development',
    productionSourceMap: false,
    configureWebpack: {
        // provide the app's title in webpack's name field, so that
        // it can be accessed in index.html to inject the correct title.
        resolve: {
            alias: {
                '@': resolve('src'),
            },
            fallback: {
                'path': false,
                'fs': false,
                "assert": false,
                //"assert": require.resolve("assert/")
                "util": false,
                //"util": require.resolve("util/")
                "os": false,
                //"os": require.resolve("os-browserify/browser")
                "crypto": false,
                //"crypto": require.resolve("crypto-browserify")

                "buffer": require.resolve("buffer/")
            }
        },
        plugins: [
            new webpack.ProvidePlugin({
                process: 'process/browser',
                Buffer: ['buffer', 'Buffer']
            }),
            ...(isProd ? [
                // gzip pre-compress static assets so nginx can serve .gz with gzip_static on
                new CompressionPlugin({
                    filename: '[path][base].gz',
                    algorithm: 'gzip',
                    test: /\.(js|css|html|svg|json)$/,
                    threshold: 10240,
                    minRatio: 0.8,
                    deleteOriginalAssets: false
                })
            ] : [])
        ],
    },
    chainWebpack(config) {
        config.plugins.delete('prefetch');
        config.when(process.env.NODE_ENV !== 'development', config => {
            config.optimization.splitChunks({
                chunks: 'all',
                maxInitialRequests: 6,
                cacheGroups: {
                    // Heavyweight wildfirechat SDK (proto + av/ptt) — defer to async chunks
                    wfcSdk: {
                        name: 'chunk-wfc-sdk',
                        test: /[\\/]src[\\/]wfc[\\/](proto|av|ptt|util)[\\/]/,
                        priority: 30,
                        chunks: 'all',
                        reuseExistingChunk: true
                    },
                    // Pinyin dictionaries (>3MB) — load lazily when contact sort is needed
                    pinyin: {
                        name: 'chunk-pinyin',
                        test: /[\\/]src[\\/]vendor[\\/]pinyin[\\/]/,
                        priority: 25,
                        chunks: 'async',
                        reuseExistingChunk: true
                    },
                    // Other vendored libs (lightbox, modal, visibility-change)
                    vendorAssets: {
                        name: 'chunk-vendor-assets',
                        test: /[\\/]src[\\/]vendor[\\/]/,
                        priority: 20,
                        chunks: 'all',
                        reuseExistingChunk: true
                    },
                    // 3rd party node_modules
                    libs: {
                        name: 'chunk-libs',
                        test: /[\\/]node_modules[\\/]/,
                        priority: 10,
                        chunks: 'initial'
                    },
                    commons: {
                        name: 'chunk-commons',
                        test: resolve('src/components'),
                        minChunks: 3,
                        priority: 5,
                        reuseExistingChunk: true
                    }
                }
            });
            config.optimization.runtimeChunk('single');
        });
        config.optimization.runtimeChunk('single');

        // Strip console & debugger in production via terser
        if (isProd) {
            config.optimization.minimizer('terser').tap((args) => {
                const opts = args[0] || {}
                opts.terserOptions = opts.terserOptions || {}
                opts.terserOptions.compress = {
                    ...(opts.terserOptions.compress || {}),
                    drop_console: true,
                    drop_debugger: true,
                    pure_funcs: ['console.log', 'console.info', 'console.debug']
                }
                opts.terserOptions.format = {
                    ...(opts.terserOptions.format || {}),
                    comments: false
                }
                opts.extractComments = false
                return args
            })
        }

        config.module
            .rule("vue")
            .use("vue-loader")
            .loader("vue-loader")
            .tap(options => {
                // Return just the compatibility config without directive transforms
                return {
                    ...options,
                    compilerOptions: {
                        compatConfig: {
                            MODE: 3
                        }
                    }
                };
            });

    }
};
