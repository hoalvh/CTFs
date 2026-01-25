FROM node:20-alpine
WORKDIR /flag-server
COPY package.json ./
COPY package-lock.json ./
RUN npm install --omit=dev
COPY . .
CMD ["npm", "start"]
