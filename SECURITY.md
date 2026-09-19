# Security policy

## Release authenticity

Before reporting a suspicious project APK, verify its signing certificate.

Expected project release certificate SHA-256:

```text
D4:1F:49:2F:0E:2A:2E:39:90:AC:7F:8E:75:CC:5D:4B:
14:89:5F:7B:46:C0:B6:11:3B:78:82:C4:8A:A5:D4:0A
```

## Reporting

For a security issue in the patch code, CI pipeline, release process, or signing assumptions, open an issue with enough detail to reproduce the problem but do not publish private credentials, signing keys, account tokens, or other secrets.

If a report would expose an active secret, contact the repository owner privately through GitHub instead of putting the secret into an issue.

## Signing key

The private Android signing key is intentionally excluded from the repository. It must never be committed, attached to an issue, or posted in logs.
