module.exports = {
  outputDir: '../docs',
  publicPath: './',
  chainWebpack: config => {
    const fileRule = config.module.rule('file')
    fileRule
      .test(/\.md$/)
      .use('file-loader')
      .loader('file-loader')
      .end()
  }
}
