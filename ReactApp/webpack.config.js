//author: Jonas Semmler
const path = require('path')
const webpack = require('webpack')
const HtmlWebPackPlugin = require('html-webpack-plugin')
const BrotliPlugin = require('brotli-webpack-plugin')

module.exports = {
  entry: './src/index.js',
  output: {
    //path: path.join(__dirname, '../src/main/resources/static/build'), //Production path
    path: path.join(__dirname, '/dist'),                                //Development path
    filename: 'bundle.js',
    publicPath: '/',
  },
  devServer: {
    port: 3001,
    historyApiFallback: true
  },
  module: {
    rules: [
      {
        test: /\.(js|jsx)$/,
        exclude: /node_modules/,
        loader: "babel-loader",
      },
      {
        test: /\.css$/i,
        use: ['style-loader', 'css-loader'],
      },
      {
        test: /\.(png|jpe?g|svg|gif)$/i,
        use: [
          {
            loader: 'file-loader',
          },
        ],
      }
    ]
  },
  plugins: [
    new HtmlWebPackPlugin({
      template: "./dist/template/index.html",
      filename: "./index.html"
    }),
     new webpack.DefinePlugin({
      'process.env.NODE_ENV': JSON.stringify('production')
     }),
     /* new BrotliPlugin({
      asset: '[path].br[query]',
      test: /\.(js|css|html|svg)$/,
      threshold: 10240,
      minRatio: 0.8
    })  */
  ]
};