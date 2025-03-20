FROM node:alpine
WORKDIR /opt/app
COPY package*.json ./
COPY . .
RUN npm install
CMD ["npm", "start"]
EXPOSE 9999