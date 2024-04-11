module.exports = {
    webpack: function (config, env) {
        config.output.filename = 'static/js/[name].[hash:8].js';
        config.output.chunkFilename = 'static/js/[name].[hash:8].chunk.js';
        return config;
    },
};