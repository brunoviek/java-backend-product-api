# Security Policy

## Supported Versions

We release patches for security vulnerabilities in the following versions:

| Version | Supported          |
| ------- | ------------------ |
| 1.0.x   | :white_check_mark: |
| < 1.0   | :x:                |

## Reporting a Vulnerability

We take the security of Product API seriously. If you believe you have found a security vulnerability, please report it to us as described below.

### Where to Report

**Please do NOT report security vulnerabilities through public GitHub issues.**

Instead, please report them via email to: security@mercadolivre.com

### What to Include

Please include the following information in your report:

- Type of vulnerability (e.g., SQL injection, XSS, authentication bypass)
- Full paths of source file(s) related to the manifestation of the vulnerability
- The location of the affected source code (tag/branch/commit or direct URL)
- Any special configuration required to reproduce the issue
- Step-by-step instructions to reproduce the issue
- Proof-of-concept or exploit code (if possible)
- Impact of the vulnerability, including how an attacker might exploit it

### What to Expect

- We will acknowledge receipt of your vulnerability report within 48 hours
- We will send a more detailed response within 7 days indicating the next steps
- We will keep you informed about the progress towards a fix
- We may ask for additional information or guidance

### Safe Harbor

We support safe harbor for security researchers who:

- Make a good faith effort to avoid privacy violations and data destruction
- Only interact with accounts you own or with explicit permission
- Do not exploit a security issue beyond what's necessary to demonstrate it
- Allow us reasonable time to address the issue before public disclosure

## Security Best Practices

When using this API in production:

### 1. Authentication & Authorization
- Implement proper authentication (OAuth2, JWT, etc.)
- Use role-based access control (RBAC)
- Never expose API keys in code or version control

### 2. Network Security
- Use HTTPS/TLS in production
- Implement rate limiting to prevent abuse
- Use API gateway for additional security layer

### 3. Data Protection
- Never log sensitive information
- Encrypt data at rest and in transit
- Implement proper input validation and sanitization

### 4. Configuration
- Use environment variables for secrets
- Never commit `.env` files
- Rotate credentials regularly

### 5. Dependencies
- Keep dependencies up to date
- Run `mvn dependency:analyze` regularly
- Monitor security advisories

### 6. Docker Security
- Always use non-root users in containers (already implemented)
- Scan images for vulnerabilities
- Keep base images updated
- Use specific version tags, avoid `latest`

### 7. Monitoring
- Enable security logging
- Monitor for unusual patterns
- Set up alerts for security events

## Known Security Considerations

### Current Implementation Status

✅ **Implemented:**
- Non-root Docker user
- Input validation via Spring validation
- Global exception handling
- Health checks
- Request ID tracing

⚠️ **Not Implemented (TODO for Production):**
- Authentication/Authorization
- Rate limiting
- HTTPS/TLS configuration
- Security headers
- CORS configuration
- Input sanitization against XSS
- SQL injection protection (using in-memory storage currently)

### Development vs Production

**Note:** This is a demonstration/educational project. Before deploying to production:

1. ✅ Implement authentication and authorization
2. ✅ Add rate limiting
3. ✅ Configure HTTPS/TLS
4. ✅ Add security headers (CSP, HSTS, etc.)
5. ✅ Implement proper CORS policies
6. ✅ Add request/response encryption for sensitive data
7. ✅ Set up Web Application Firewall (WAF)
8. ✅ Implement comprehensive audit logging
9. ✅ Add DDoS protection
10. ✅ Regular security audits and penetration testing

## Security Contacts

- Security Team: security@mercadolivre.com
- Project Maintainer: bruno@mercadolivre.com

## Acknowledgments

We appreciate the security community's efforts in responsibly disclosing vulnerabilities and helping us maintain a secure project.
