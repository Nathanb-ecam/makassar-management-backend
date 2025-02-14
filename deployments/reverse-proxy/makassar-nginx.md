# Makassar Management api deployment

## Nginx configuration

1. First setup an http forwarder (certificates will be generated later):

   In the /etc/nginx/sites-available/ directory, create a new config file "domain.be" using the makassar-nginx.conf template.

2. Create a symbolic link between site-enabled and available with:

   ```bash
   sudo ln -s /etc/nginx/sites-available/domain.be /etc/nginx/sites-enabled/
   ```

3. Test the nginx configuration with :

   ```bash
   sudo nginx -t
   ```

4. To apply config changes:

   ```bash
   sudo systemctl reload nginx
   ```

## SSL certificates

1. Install certbot and nginx plugin:

   ```bash
   sudo apt update
   sudo apt install certbot python3-certbot-nginx
   ```

2. Obtain SSL certificates :

   The domain name must match the name of the config file in /etc/nginx/site-available.

   ```bash
   sudo certbot --nginx -d domain.be -d www.domain.be
   ```

3. To test manually the cert renewal:

   ```bash
   sudo certbot renew --dry-run
   ```
