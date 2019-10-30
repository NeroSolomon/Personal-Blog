## postMessage
a.html
```html
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8">
    <title>外部html</title>
</head>
<body>
<div>
    <input id="text" type="text" value="42度空间" />
    <button id="send" >发送消息</button>
</div>
<iframe id="receiver" src="http://localhost:8081/b.html" width="500" height="60">
    <p>你的浏览器不支持IFrame。</p>
</iframe>
<script>
    window.onload = function() {
        var receiver = document.getElementById('receiver').contentWindow;
        var btn = document.getElementById('send');
        btn.addEventListener('click', function (e) {
            e.preventDefault();
            var val = document.getElementById('text').value;
            receiver.postMessage("Hello "+val+"！", "http://localhost:8081/");
        });
    }
</script>
</body>
</html>
```

b.html
```html
<!DOCTYPE HTML>
<html>
<head>
    <meta charset="utf-8">
    <title>内嵌的html</title>
</head>
<body>
<div id="message">
  B.html
</div>
<script>
    window.onload = function() {
        var messageEle = document.getElementById('message');
        window.addEventListener('message', function (e) {
            console.log(e.data)
        });
    }
</script>
</body>
</html>
```