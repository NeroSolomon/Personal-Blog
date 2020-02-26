## 笔记
```js
const express = require('express');
const serveStatic = require('serve-static');
const history = require('connect-history-api-fallback');

const app = express();

app.use(serveStatic('dist', { index: ['index.html'] }));

app.use(function(req, res) {
  // respond with html page
  if (req.accepts('html')) {
    res.sendFile('dist/index.html', { root: './' });
    return;
  }

  // respond with json
  if (req.accepts('json')) {
    res.send({ error: 'Not found' });
    return;
  }

  // default to plain-text. send()
  res.type('txt').send('Not found');
});

app.use(history());

app.listen(3000, err => {
  if (err) {
    return;
  }
});
```