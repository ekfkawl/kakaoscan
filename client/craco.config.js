const { whenProd } = require('@craco/craco');
const HtmlWebpackPlugin = require('html-webpack-plugin');

module.exports = {
    webpack: {
        configure: (webpackConfig, { env, paths }) => {
            return whenProd(() => {
                webpackConfig.plugins = webpackConfig.plugins.map((plugin) => {
                    if (plugin instanceof HtmlWebpackPlugin) {
                        return new HtmlWebpackPlugin({
                            ...plugin.options,
                            cache: false,
                            hash: true,
                        });
                    }

                    return plugin;
                });

                return webpackConfig;
            }, webpackConfig);
        },
    },
};
