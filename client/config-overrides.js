// const HtmlWebpackPlugin = require('html-webpack-plugin');
// const CssMinimizerPlugin = require('css-minimizer-webpack-plugin');
// const TerserPlugin = require('terser-webpack-plugin');
//
// module.exports = {
//     webpack: function (config, env) {
//         config.output.filename = 'static/js/[name].[hash:8].js';
//         config.output.chunkFilename = 'static/js/[name].[hash:8].chunk.js';
//
//         config.plugins = config.plugins.map((plugin) => {
//             if (plugin instanceof HtmlWebpackPlugin) {
//                 return new HtmlWebpackPlugin({
//                     ...plugin.options,
//                     minify: {
//                         removeComments: true,
//                         collapseWhitespace: true,
//                         removeRedundantAttributes: true,
//                         useShortDoctype: true,
//                         removeEmptyAttributes: true,
//                         removeStyleLinkTypeAttributes: true,
//                         keepClosingSlash: true,
//                         minifyJS: true,
//                         minifyCSS: true,
//                         minifyURLs: true,
//                     },
//                 });
//             }
//             return plugin;
//         });
//
//         config.optimization = {
//             minimize: true,
//             minimizer: [
//                 new CssMinimizerPlugin(),
//                 new TerserPlugin({
//                     terserOptions: {
//                         compress: {
//                             drop_console: true,
//                         },
//                     },
//                 }),
//             ],
//         };
//
//         return config;
//     },
// };
