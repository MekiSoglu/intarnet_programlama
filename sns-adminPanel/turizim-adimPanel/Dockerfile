# 1️⃣ Angular için Node.js görüntüsünü kullanarak bağımlılıkları yükle
FROM node:18 as build

WORKDIR /app

# Package.json ve package-lock.json dosyalarını kopyala ve bağımlılıkları yükle
COPY package.json package-lock.json ./
RUN npm install

# Angular kaynak kodlarını kopyala ve üretim için build al
COPY . .
RUN npm run build -- --configuration=production --output-path=/app/dist

# 2️⃣ Nginx ile Angular uygulamasını yayınlama
FROM nginx:1.23

# Nginx için default.conf dosyasını kopyala
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Angular build edilmiş dosyaları Nginx’in html dizinine kopyala
COPY --from=build /app/dist/browser/. /usr/share/nginx/html

# Portu aç
EXPOSE 80

# Nginx'i çalıştır
CMD ["nginx", "-g", "daemon off;"]
