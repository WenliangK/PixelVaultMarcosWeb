Para Windows quitar "sudo"

Encender los servicios (Base de datos y Backend): Ubícate en la raíz del proyecto y ejecuta el siguiente comando para compilar y encender todo en segundo plano: sudo docker-compose up --build -d

Apagar los servicios: Detiene y elimina de forma limpia los contenedores manteniendo los datos seguros en el volumen: 
sudo docker-compose down

Verificar si los servicios están activos (UP): Muestra la lista de contenedores en ejecución actual con sus puertos y estados: 
sudo docker ps

Ver los registros (logs) del Backend en vivo: Permite monitorear la consola de Spring Boot para revisar la conexión con la base de datos o errores en tiempo real (para salir presiona Ctrl + C): 
sudo docker logs -f pixelvault-backend

Entrar a la Base de Datos (PostgreSQL) desde la consola: Abre la interfaz interactiva de comandos dentro del contenedor de la base de datos: 
sudo docker exec -it pixelvault-db psql -U postgres -d pixelvault_db

Ver las tablas estando dentro de la base de datos: Una vez dentro de la consola de PostgreSQL (pixelvault_db=#), lista todas las tablas creadas:  
\dt

Salir de la consola de la Base de datos: Regresa a la terminal normal de tu sistema operativo: 
\q