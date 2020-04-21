const path = require('path')
const webpack = require('webpack')
const HtmlWebPackPlugin = require('html-webpack-plugin')

module.exports = {
    entry: "./src/App.js",
    output: {
      path: path.resolve(__dirname, '../src/main/resources/static/build'),
      publicPath: '/react-components/dist/',
      filename: 'build.js'
    },
    module: {
      rules: [
        {
          test: /\.(js|jsx)$/,
          exclude: /node_modules/,
          use: {
            loader: "babel-loader"
          }
        },
          {
            test: /\.html$/,
            use: [
              {
                loader: "html-loader"
              }
            ]
          }
      ]
    },
    plugins: [
      new HtmlWebPackPlugin({
        template: "./src/template/index.html",
        filename: "../../ReactApp/index.html"
      })
    ]
  };