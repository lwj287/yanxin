@echo off
echo Starting all backend services...

start "anxinbao" cmd /c "cd backend\anxinbao && mvn spring-boot:run"
start "bi" cmd /c "cd backend\bi && mvn spring-boot:run"
start "keyingmen" cmd /c "cd backend\keyingmen && mvn spring-boot:run"
start "suoguanjia" cmd /c "cd backend\suoguanjia && mvn spring-boot:run"
start "xueyuan" cmd /c "cd backend\xueyuan && mvn spring-boot:run"
start "zhaoxianbao" cmd /c "cd backend\zhaoxianbao && mvn spring-boot:run"
start "zhijian" cmd /c "cd backend\zhijian && mvn spring-boot:run"
start "zhipaidan" cmd /c "cd backend\zhipaidan && mvn spring-boot:run"

echo All backend services have been triggered to start.
