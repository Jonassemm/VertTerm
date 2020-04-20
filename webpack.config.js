var path = require('path');

module.exports = {
	mode: 'development',
	entry:{
			index: './src/main/js/App.js',
			userListDisplay: './src/main/js/userListComponent.js'
		},
    output: {
        filename: '[name].js',
        path: __dirname + '/src/main/resources/static/built/',
    },
	module: {
		rules: [
			{
				test: path.join(__dirname, '.'),
				exclude: /(node_modules)/,
				use: [{
					loader: 'babel-loader',
					options: {
						presets: ["@babel/preset-env", "@babel/preset-react"]
					}
				}]
			}
		]
	}
};