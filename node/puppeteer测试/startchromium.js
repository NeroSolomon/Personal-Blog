const puppeteer = require('puppeteer');

(async () => {
  const browser = await puppeteer.launch();
  const page = await browser.newPage();
  console.log(process.env.TESTURL)
  await page.goto('https://example.com');
  await page.screenshot({path: 'example.png'});

  await browser.close();
})();