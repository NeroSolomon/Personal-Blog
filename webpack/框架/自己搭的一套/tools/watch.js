import webpack from 'webpack';
import ora from 'ora';
import chalk from 'chalk';
import getConfig from '../webpack.config';

const chalkError = chalk.red;
const chalkSuccess = chalk.green;
const chalkWarning = chalk.yellow;
const chalkProcessing = chalk.blue;

const env = 'production';

// 提示信息
console.log(
  chalkProcessing(
    `Generating minified bundle for ${env} via Webpack. This will take a moment...`
  )
);
// loading
const spinner = ora(`building for ${env}...`);
spinner.start();

const config = getConfig(env);
webpack(config).watch({
  // [watchOptions](/configuration/watch/#watchoptions) 示例
  aggregateTimeout: 300,
  ignored: /node_modules/,
  poll: undefined
}, (error, stats) => {
  if (error) {
    console.log(chalkError(error));
    return;
  }
  const jsonStats = stats.toJson();
  if (jsonStats.hasErrors) {
    return jsonStats.errors.map((error) => console.log(chalkError(error)));
  } else if (jsonStats.hasWarnings) {
    console.log(chalkWarning('Webpack generated the following warnings: '));
    jsonStats.warnings.map((warning) => console.log(chalkWarning(warning)));
  }

  spinner.stop();

  console.log(
    stats.toString({
      colors: true,
      hash: true,
      version: true,
      children: false,
      chunks: false,
      modules: false,
      chunkModules: false,
    })
  );

  console.log(
    chalkSuccess(
      `Your app is watching at ${env} mode in /dist. It's ready to roll!`
    )
  );
});