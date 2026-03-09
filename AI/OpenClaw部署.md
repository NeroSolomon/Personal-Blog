1. 安装Homebrew
```bash
/bin/bash -c "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/HEAD/install.sh)"
```
2. 安装nodejs 22 以上
```bash
brew install node@22
```
3. 安装openclaw
```bash
curl -fsSL https://openclaw.ai/install.sh | bash
# 或者
npm install -g openclaw@latest
```
4. 验证openclaw
```bash
openclaw status
```
5. 设置gateway
```bash
openclaw config set gateway.mode local
```
6. 配置
```bash
vim ~/.openclaw/openclaw.json
```

```txt
{
  "meta": {
    "lastTouchedVersion": "2026.3.7",
    "lastTouchedAt": "2026-03-09T05:09:52.991Z"
  },
  "models": {
    "mode": "merge",
    "providers": {
      "claude": {
        "baseUrl": "https://xxxx/v1",
        "apiKey": "apiKey",
        "auth": "api-key",
        "api": "openai-completions",
        "models": [
          {
            "id": "claude-opus-4-6",
            "name": "Claude Chat"
          }
        ]
      }
    }
  },
  "agents": {
    "defaults": {
      "model": {
        "primary": "claude/claude-opus-4-6"
      },
      "models": {
        "claude/claude-opus-4-6": {}
      },
      "workspace": "/Users/user1/.openclaw/workspace",
      "compaction": {
        "mode": "safeguard"
      }
    }
  },
  "commands": {
    "native": "auto",
    "nativeSkills": "auto",
    "restart": true,
    "ownerDisplay": "raw"
  },
  "gateway": {
    "mode": "local",
    "auth": {
      "mode": "token",
      "token": "xxx"
    }
  }
}
```

7. 打开使用
```bash
openclaw dashboard
```