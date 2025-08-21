FROM node:20.10.0
WORKDIR /usr/src/app

COPY package*.json ./
RUN npm install --no-audit --no-fund

COPY ./ ./
RUN npm run build

EXPOSE 3000
CMD ["npx","--yes","serve@latest","-s","build","-l","tcp://0.0.0.0:3000"]