'use strict';

var webpack = require('webpack'),
    path = require('path'),
    srcPath = path.join(__dirname, 'src');

var config = {
    target: 'web',
    entry: {
        app: path.join(srcPath, 'app.jsx')
        //, common: ['react-dom', 'react']
    },
    output: {
        path:path.resolve(__dirname, 'public/javascripts'),
        filename: 'bundle.js',
    },

    module: {
        rules: [
            {
                test: /\.jsx?$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ["react", "es2015"]
                    }
                }
            },
            {
                test: /\.scss$/,
                include: /\/app\/assets/,
                loader: 'style!css!sass'
            }
        ]
    },
    plugins: [
        //new webpack.optimize.CommonsChunkPlugin('common', 'common.js'),
        new webpack.optimize.UglifyJsPlugin({
            compress: { warnings: false },
            output: { comments: false }
        })
    ]
};

module.exports = config;
