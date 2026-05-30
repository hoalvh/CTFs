FROM node:20-slim
RUN apt-get update && apt-get install -y \
    chromium fonts-liberation libgbm1 libnss3 libxss1 \
    --no-install-recommends && rm -rf /var/lib/apt/lists/*
ENV PUPPETEER_EXECUTABLE_PATH=/usr/bin/chromium
ENV PUPPETEER_SKIP_CHROMIUM_DOWNLOAD=true
WORKDIR /app
COPY src/package.json .
RUN npm install --production
COPY src/app.js src/bot.js ./
COPY src/views/ views/
COPY src/public/ public/
EXPOSE 3000
CMD ["node", "app.js"]
