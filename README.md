-- ===============================================
---------------CREAR USUARIO GOSTAY---------------
-- ===============================================
ALTER SESSION SET "_ORACLE_SCRIPT"=true;
CREATE USER GoStay IDENTIFIED by "ricaldone2024e";
grant "CONNECT" TO GoStay;

-- ===============================================
---------------BORRAR USUARIO GOSTAY--------------
-- ===============================================
ALTER SESSION SET "_ORACLE_SCRIPT"=true;
DROP USER GoStay CASCADE;

-- ===============================================
-------------TABLA DE TIPO DE USUARIOS------------
-- ===============================================
CREATE TABLE tbTiposUsuarios(
id_tipo_usuario INT PRIMARY KEY,
nombre_usuario VARCHAR2(25) NOT  NULL
);

--SECUENCIA DE LA TABLA TIPO DE USUARIOS--
CREATE SEQUENCE identity_TiposUsuarios
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGER DE LA TABLA TIPO DE USUARIOS--
CREATE OR REPLACE TRIGGER trg_before_insert_tbTipoUsuario
BEFORE INSERT ON tbTiposUsuarios
FOR EACH ROW
BEGIN
    SELECT identity_TiposUsuarios.NEXTVAL INTO :new.id_tipo_usuario FROM dual;
END;

--INSERTS DE LA TABLA TIPO DE USUARIOS--
INSERT ALL
INTO tbTiposUsuarios(nombre_usuario) values ('Cliente')
INTO tbTiposUsuarios(nombre_usuario) values ('Gerente')
INTO tbTiposUsuarios(nombre_usuario) values ('ADMIN')
SELECT * FROM dual;

SELECT * FROM tbTiposUsuarios;

--PARA PODER BORRAR LA TABLA DE TIPO DE USUARIOS--
DROP TABLE tbTiposUsuarios CASCADE CONSTRAINTS;
DROP SEQUENCE identity_TiposUsuarios;

-- ===============================================
---------------TABLA DE USUARIOS------------------
-- ===============================================
CREATE TABLE tbUsuarios(
id_usuario INT PRIMARY KEY,
nombre_usuario VARCHAR2(25) NOT NULL,
apellido VARCHAR2(25) NOT NULL,
fecha_nacimiento VARCHAR2(25) NOT NULL,
correo VARCHAR2(40) NOT NULL UNIQUE,
telefono VARCHAR2(20) NOT NULL,
contraseña VARCHAR2(70) NOT NULL CHECK (LENGTH(contraseña) >= 8),
id_tipo_usuario INT,--cambio
CONSTRAINT FKid_tipo_usuario FOREIGN KEY(id_tipo_usuario) REFERENCES tbTiposUsuarios(id_tipo_usuario) ON DELETE CASCADE
);

--SE AGREGO UN NUEVO CAMPO A LA TABLA DE USUARIOS--
ALTER TABLE tbUsuarios
ADD imgFoto VARCHAR2(500);

--SECUENCIA DE LA TABLA USUARIOS--
CREATE SEQUENCE identity_Usuarios
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGERS DE LA TABLA DE USUARIOS--
CREATE OR REPLACE TRIGGER trg_update_tbUsuarios
AFTER UPDATE OF id_tipo_usuario ON tbTiposUsuarios
FOR EACH ROW
BEGIN
    UPDATE tbUsuarios
    SET id_tipo_usuario = :new.id_tipo_usuario
    WHERE id_tipo_usuario = :old.id_tipo_usuario;
END;

CREATE OR REPLACE TRIGGER trg_before_insert_tbUsuarios
BEFORE INSERT ON tbUsuarios
FOR EACH ROW
BEGIN
    SELECT identity_Usuarios.NEXTVAL INTO :new.id_usuario FROM dual;
END;

--INSERTS A LA TABLA DE USUARIOS--
INSERT ALL
                                                                                                                                                                                    --perdo12345678--
INTO tbusuarios(nombre_usuario, apellido, fecha_nacimiento, correo, telefono, contraseña, id_tipo_usuario, imgFoto) values ('Pedro', 'Guzman', '17/4/1980', 'perdo_guzman@gmail.com', '8764-2321', '8bb7a6fa06b2468f9ca987c9c6ab039746460c876a8214f47f734561f1a5a382', 1, 'nueva_url_de_imagen1')
                                                                                                                                                                                    --paco12345678--
INTO tbusuarios(nombre_usuario, apellido, fecha_nacimiento, correo, telefono, contraseña, id_tipo_usuario, imgFoto) values ('Paco', 'Lopez', '28/3/1998', 'paco_lopez@gmail.com', '2364-2901', '998794d2dbedaaf80d0cf80bbfe23a7b7767362f83c7b3207fedb1290dbb44c3 ', 1, 'nueva_url_de_imagen2')
                                                                                                                                                                                    --ismael12345678--
INTO tbusuarios(nombre_usuario, apellido, fecha_nacimiento, correo, telefono, contraseña, id_tipo_usuario, imgFoto) values ('Ismael', 'Perez', '09/4/1993', 'ismael_perez@gmail.com', '1324-3201', '8D21CE1F9DD8FB1BB42D24AD4C55688E9DD05D34FA24241C81E31F413214E6B8', 1, 'nueva_url_de_imagen3')
                                                                                                                                                                                    --perdo12345678--
INTO tbusuarios(nombre_usuario, apellido, fecha_nacimiento, correo, telefono, contraseña, id_tipo_usuario, imgFoto) values ('Juan', 'Torres', '12/9/1999', 'juan_torres@gmail.com', '2345-2501', '8BB7A6FA06B2468F9CA987C9C6AB039746460C876A8214F47F734561F1A5A382', 1, 'nueva_url_de_imagen4')
                                                                                                                                                                                    --gabi1234567890--
INTO tbusuarios(nombre_usuario, apellido, fecha_nacimiento, correo, telefono, contraseña, id_tipo_usuario, imgFoto) values ('Paco', 'Lopez', '28/3/1998', 'gabi@gmail.com', '1554-1234', 'e3d55e763f59cd9b1e3b3b786ae29974eee23b70d4e5aa390f04133371f6a91b', 3, 'nueva_url_de_imagen5')
SELECT * FROM dual;

SELECT * FROM tbUsuarios;

--SE ACTUALIZO EL CAMPO IMGFOTO DE LA TABLA DE USUARIOS--
UPDATE tbUsuarios
SET imgFoto = 'nueva_url_de_imagen1'
WHERE id_usuario = 1;

--PARA PODER BORRAR LA TABLA DE USUARIOS--
DROP TABLE tbusuarios CASCADE CONSTRAINTS;
DROP SEQUENCE identity_Usuarios;

-- ===============================================
-----TABLA DE TIPOS DE TIPOS DE HABITACIONES------
-- ===============================================
CREATE TABLE tbTiposHabitaciones(
id_tipo_habitacion INT PRIMARY KEY,
nombre_tipo_habitacion VARCHAR2(40) NOT NULL,
capacidad_habitacion INT NOT NULL CHECK (capacidad_habitacion < 11),
precio_habitacion NUMBER(5,2) NOT NULL CHECK (precio_habitacion >= 0),
img_tipo_habitacion VARCHAR2(255)NOT NULL
);

--SECUENCIA DE LA TABLA DE TIPOS DE HABITACIONES--
CREATE SEQUENCE identity_TiposHabitacion
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGERS DE LA TABLA DE TIPOS DE HABITACIONES--
CREATE OR REPLACE TRIGGER trg_before_insert_tbTiposHabitaciones
BEFORE INSERT ON tbTiposHabitaciones
FOR EACH ROW
BEGIN
    SELECT identity_TiposHabitacion.NEXTVAL INTO :new.id_tipo_habitacion FROM dual;
END;

--INSERTS DE LA TABLA DE TIPOS DE HABITACIONES--
INSERT ALL
INTO tbTiposHabitaciones (nombre_tipo_habitacion, capacidad_habitacion, precio_habitacion, img_tipo_habitacion) VALUES ('Habitación Doble', 2, 100,'https://www.ghlhoteles.com/cache/4f/56/4f56b540dec02093f24f38473594cecf.jpg')
INTO tbTiposHabitaciones (nombre_tipo_habitacion, capacidad_habitacion, precio_habitacion, img_tipo_habitacion) VALUES ('Habitación Deluxe', 4,  200,'https://www.oirealestate.net/noticias-inmobiliarias/wp-content/uploads/2022/03/9-5.jpg')
INTO tbTiposHabitaciones (nombre_tipo_habitacion, capacidad_habitacion, precio_habitacion, img_tipo_habitacion) VALUES ('Habitación Ejecutiva', 1,  80,'https://www.cemix.com/wp-content/uploads/2023/01/habitaciones-modernas-tipos.jpg')
INTO tbTiposHabitaciones (nombre_tipo_habitacion, capacidad_habitacion, precio_habitacion, img_tipo_habitacion) VALUES ('Habitación Twin', 2,  150,'https://www.livitum.com/blogs/8/9/0/tx_52a2f341-1eb1-4771-8800-b3640f509414_habitacion_juvenil_10.jpg')
INTO tbTiposHabitaciones (nombre_tipo_habitacion, capacidad_habitacion, precio_habitacion, img_tipo_habitacion) VALUES ('Suite Junior', 1,  180,'https://content.arquitecturaydiseno.es/medio/2024/01/09/dsc-6279_00000000_5a699724_240109125620_1280x794.jpg')
SELECT * FROM dual;

SELECT * FROM tbTiposHabitaciones;

--PARA PODER BORRAR LA TABLA DE TIPOS DE HABITACIONES--
DROP TABLE tbTiposHabitaciones CASCADE CONSTRAINTS;
DROP SEQUENCE identity_TiposHabitacion;

-- ===============================================
--------TABLA DE SERVICIOS DE HABITACIÓN----------
-- ===============================================
CREATE TABLE tbServiciosHabitacion(
id_servicio_habitacion INT PRIMARY KEY,
nombre_servicio_habitacion VARCHAR2(40) NOT NULL,
img_icono_habitacion VARCHAR2(255) NOT NULL,
id_tipo_habitacion INT,
CONSTRAINT FK_idtipohabitacion_tbservicioshabitacion FOREIGN KEY (id_tipo_habitacion) REFERENCES tbTiposHabitaciones(id_tipo_habitacion) ON DELETE CASCADE
);

--SECUENCIA DE LA TABLA SERVICIOS DE HABITACIÓN--
CREATE SEQUENCE identity_ServiciosHabitacion
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGER DE LA TABLA SERVICIOS DE HABITACIÓN--
CREATE OR REPLACE TRIGGER trg_before_insert_tbServiciosHabitacion
BEFORE INSERT ON tbServiciosHabitacion
FOR EACH ROW
BEGIN
    SELECT identity_ServiciosHabitacion.NEXTVAL INTO :new.id_servicio_habitacion FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_update_tbServiciosHabitacion_tipohabitacion
AFTER UPDATE OF id_tipo_habitacion ON tbTiposHabitaciones
FOR EACH ROW
BEGIN
    UPDATE tbServiciosHabitacion
    SET id_tipo_habitacion = :new.id_tipo_habitacion
    WHERE id_tipo_habitacion = :old.id_tipo_habitacion;
END;

--INSERTS PARA LA TABLA SERVICIOS DE HABITACIÓN--
INSERT ALL
INTO tbServiciosHabitacion (img_icono_habitacion, nombre_servicio_habitacion, id_tipo_habitacion) VALUES ('https://static.vecteezy.com/system/resources/previews/010/332/096/non_2x/wind-flat-color-outline-icon-free-png.png','Aire acondicionado y calefacción', 1)
INTO tbServiciosHabitacion (img_icono_habitacion, nombre_servicio_habitacion, id_tipo_habitacion) VALUES ('https://png.pngtree.com/png-vector/20191113/ourlarge/pngtree-television-icon-simple-style-png-image_1966270.jpg','Televisión', 1)
INTO tbServiciosHabitacion (img_icono_habitacion, nombre_servicio_habitacion, id_tipo_habitacion) VALUES ('https://static.vecteezy.com/system/resources/previews/017/591/034/non_2x/ocean-view-with-palm-icon-outline-illustration-vector.jpg','Vista al mar', 1)
INTO tbServiciosHabitacion (img_icono_habitacion, nombre_servicio_habitacion, id_tipo_habitacion) VALUES ('https://cdn-icons-png.flaticon.com/512/5900/5900039.png','Jacuzzi', 1)
INTO tbServiciosHabitacion (img_icono_habitacion, nombre_servicio_habitacion, id_tipo_habitacion) VALUES ('https://cdn-icons-png.flaticon.com/512/227/227143.png','Máquina de café/tetera', 1)
SELECT * FROM dual;

SELECT * FROM tbServiciosHabitacion;

--PARA PODER BORRAR LA TABLA DE SERVICIOS DE HABITACIÓN--
DROP TABLE tbServiciosHabitacion CASCADE CONSTRAINTS;
DROP SEQUENCE identity_ServiciosHabitacion;

-- ===============================================
----------TABLA DE SERVICIOS DE HOTEL-------------
-- ===============================================
CREATE TABLE tbServiciosHotel(
id_servicio_hotel INT PRIMARY KEY,
nombre_servicio VARCHAR2(40) NOT NULL,
img_icono_hotel VARCHAR2(255)NOT NULL
);

--SECUENCIA DE LA TABLA DE SERVICIOS DE HOTEL--
CREATE SEQUENCE identity_ServiciosHotel
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGER DE LA TABLA DE SERVICIOS DE HOTEL--
CREATE OR REPLACE TRIGGER trg_before_insert_tbServiciosHotel
BEFORE INSERT ON tbServiciosHotel
FOR EACH ROW
BEGIN
    SELECT identity_ServiciosHotel.NEXTVAL INTO :new.id_servicio_hotel FROM dual;
END;

--INSERTS DE LA TABLA DE SERVICIOS DE HOTEL--
INSERT ALL
INTO tbServiciosHotel (nombre_servicio,img_icono_hotel) VALUES ('Pet friendly','https://i.imgur.com/DabZS1b.jpeg')
INTO tbServiciosHotel (nombre_servicio,img_icono_hotel) VALUES ('Wifi gratis','https://i.imgur.com/kBUkItj.jpeg')
INTO tbServiciosHotel (nombre_servicio,img_icono_hotel) VALUES ('Restaurantes','https://i.imgur.com/2dxfMa6.jpeg')
INTO tbServiciosHotel (nombre_servicio,img_icono_hotel) VALUES ('Parqueo','https://i.imgur.com/NOVC1nL.jpeg')
INTO tbServiciosHotel (nombre_servicio,img_icono_hotel) VALUES ('Piscinas','https://i.imgur.com/uQZt83u.jpeg')
SELECT * FROM dual;

SELECT * FROM tbServiciosHotel;

--PARA PODER BORRAR LA TABLA DE SERVICIOS DE HOTEL--
DROP TABLE tbServiciosHotel CASCADE CONSTRAINTS;
DROP SEQUENCE identity_ServiciosHotel;

-- ===============================================
-------------TABLA DE DEPARTAMENTOS---------------
-- ===============================================
CREATE TABLE tbDepartamentos(
id_departamento INT PRIMARY KEY,
nombre_departamento VARCHAR2(25) NOT NULL
);

--SECUENCIA DE LA TABLA DE DEPARTAMENTOS--
CREATE SEQUENCE identity_Departamentos
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGER DE LA TABLA DE DEPARTAMENTOS--
CREATE OR REPLACE TRIGGER trg_before_insert_tbDepartamentos
BEFORE INSERT ON tbDepartamentos
FOR EACH ROW
BEGIN
    SELECT identity_Departamentos.NEXTVAL INTO :new.id_departamento FROM dual;
END;

--INSERTS DE LA TABLA DE DEPARTAMENTOS--
INSERT ALL
INTO tbDepartamentos (nombre_departamento) VALUES ('Ahuachapán')
INTO tbDepartamentos (nombre_departamento) VALUES ('Cabañas')
INTO tbDepartamentos (nombre_departamento) VALUES ('Chalatenango')
INTO tbDepartamentos (nombre_departamento) VALUES ('Cuscatlán')
INTO tbDepartamentos (nombre_departamento) VALUES ('La Libertad')
INTO tbDepartamentos (nombre_departamento) VALUES ('La Paz')
INTO tbDepartamentos (nombre_departamento) VALUES ('La Unión')
INTO tbDepartamentos (nombre_departamento) VALUES ('Morazán')
INTO tbDepartamentos (nombre_departamento) VALUES ('San Miguel')
INTO tbDepartamentos (nombre_departamento) VALUES ('San Salvador')
INTO tbDepartamentos (nombre_departamento) VALUES ('San Vicente')
INTO tbDepartamentos (nombre_departamento) VALUES ('Santa Ana')
INTO tbDepartamentos (nombre_departamento) VALUES ('Sonsonate')
INTO tbDepartamentos (nombre_departamento) VALUES ('Usulután')
SELECT * FROM dual;

SELECT * FROM tbDepartamentos;

--PARA PODER BORRAR LA TABLA DE DEPARTAMENTOS--
DROP TABLE tbDepartamentos CASCADE CONSTRAINTS;
DROP SEQUENCE identity_Departamentos;

-- ===============================================
--------------TABLA DE CALIFICACIÓN---------------
-- ===============================================
CREATE TABLE tbCalificación(
id_calificación INT PRIMARY KEY,
nombre_calificación VARCHAR2(50) NOT NULL
);

--SECUENCIA DE LA TABLA DE CALIFICACIÁ“N--
CREATE SEQUENCE identity_Calificación
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGER DE LA TABLA DE CALIFICACIÓN--
CREATE OR REPLACE TRIGGER trg_before_insert_tbCalificación
BEFORE INSERT ON tbCalificación
FOR EACH ROW
BEGIN
    SELECT identity_Calificación.NEXTVAL INTO :new.id_calificación FROM dual;
END;

--INSERTS DE LA TABLA DE CALIFICACIÓN--
INSERT ALL
INTO tbCalificación (nombre_calificación) VALUES ('Muy malo')
INTO tbCalificación (nombre_calificación) VALUES ('Malo')
INTO tbCalificación (nombre_calificación) VALUES ('Regular')
INTO tbCalificación (nombre_calificación) VALUES ('Bueno')
INTO tbCalificación (nombre_calificación) VALUES ('Excelente')
SELECT * FROM dual;

SELECT * FROM tbCalificación;

--PARA PODER BORRAR LA TABLA DE CALIFICACIÓN--
DROP TABLE tbDepartamentos CASCADE CONSTRAINTS;
DROP SEQUENCE identity_Departamentos;

-- ===============================================
--------------TABLA DE VALORACIONES---------------
-- ===============================================
CREATE TABLE tbValoraciones(
id_valoracion INT PRIMARY KEY,
comentario VARCHAR2(500) NOT NULL,
id_usuario INT,
id_hoteles INT,
CONSTRAINT FK_tbValoraciones_idusuario FOREIGN KEY(id_usuario) REFERENCES tbUsuarios(id_usuario) ON DELETE CASCADE,
CONSTRAINT FK_tbHoteles_idHoteles FOREIGN KEY(id_hoteles) REFERENCES tbHoteles(id_hoteles) ON DELETE CASCADE
);

SELECT vl.id_valoracion, vl.comentario, vl.id_usuario, us.nombre_usuario, us.imgfoto  
FROM tbValoraciones vl
INNER JOIN tbUsuarios us ON vl.id_usuario = us.id_usuario
WHERE vl.id_hoteles = ?;

--SECUENCIA DE LA TABLA DE VALORACIONES--
CREATE SEQUENCE identity_Valoraciones
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGERS DE LA TABLA DE VALORACIONES--
CREATE OR REPLACE TRIGGER trg_before_insert_tbValoraciones
BEFORE INSERT ON tbValoraciones
FOR EACH ROW
BEGIN
    SELECT identity_Valoraciones.NEXTVAL INTO :new.id_valoracion FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_update_tbValoraciones_usuario
AFTER UPDATE OF id_usuario ON tbUsuarios
FOR EACH ROW
BEGIN
    UPDATE tbValoraciones
    SET id_usuario = :new.id_usuario
    WHERE id_usuario = :old.id_usuario;
END;

CREATE OR REPLACE TRIGGER trg_update_tbValoraciones_hoteles
AFTER UPDATE OF id_hoteles ON tbHoteles
FOR EACH ROW
BEGIN
    UPDATE tbValoraciones
    SET id_hoteles = :new.id_hoteles
    WHERE id_hoteles = :old.id_hoteles;
END;

--INSERTS DE LA TABLA DE VALORACIONES--
INSERT ALL
INTO tbValoraciones(comentario, id_usuario, id_hoteles) VALUES ('El personal fue muy amable y la vista al mar desde nuestra habitación era espectacular. Definitivamente volveremos.', 13, 2)
INTO tbValoraciones(comentario, id_usuario, id_hoteles) VALUES ('Las habitaciones eran muy cómodas y limpias.', 1,2)
INTO tbValoraciones(comentario, id_usuario, id_hoteles) VALUES ('Ubicación perfecta, cerca de todos los puntos turísticos.', 3,1)
INTO tbValoraciones(comentario, id_usuario, id_hoteles) VALUES ('Habitaciones amplias y limpias. El personal fue extremadamente atento y servicial durante toda nuestra estancia.', 5,1)
INTO tbValoraciones(comentario, id_usuario, id_hoteles) VALUES ('Perfecto para una escapada de fin de semana. ', 4,1)
SELECT * FROM dual;

SELECT * FROM tbValoraciones;

--PARA PODER BORRAR LA TABLA DE VALORACIONES--
DROP TABLE tbValoraciones CASCADE CONSTRAINTS;
DROP SEQUENCE identity_Valoraciones;

-- ===============================================
-----------------TABLA DE HOTELES-----------------
-- ===============================================
CREATE TABLE tbHoteles(
id_hoteles INT PRIMARY KEY,
nombre VARCHAR2(200) NOT NULL,
descripcion VARCHAR2(1000) NOT NULL,
direccion VARCHAR2(150) NOT NULL UNIQUE,
latitudHotel NUMBER(15,10) NOT NULL,
longitudHotel NUMBER(15,10) NOT NULL,
correo VARCHAR2(100) NOT NULL,
cantidad_habitaciones INT NOT NULL CHECK (cantidad_habitaciones >= 0),
img_url VARCHAR2(250) NOT NULL,
id_usuario INT,
CONSTRAINT fk_usuario_tbhoteles FOREIGN KEY (id_usuario) REFERENCES tbUsuarios(id_usuario) ON DELETE CASCADE
);

--SECUENCIA DE LA TABLA DE HOTELES--
CREATE SEQUENCE identity_Hoteles
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGERS DE LA TABLA DE HOTELES--
CREATE OR REPLACE TRIGGER trg_before_insert_tbHoteles
BEFORE INSERT ON tbHoteles
FOR EACH ROW
BEGIN
    SELECT identity_Hoteles.NEXTVAL INTO :new.id_hoteles FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_update_tbHoteles_usuario
AFTER UPDATE OF id_usuario ON tbUsuarios
FOR EACH ROW
BEGIN
    UPDATE tbHoteles
    SET id_usuario = :new.id_usuario
    WHERE id_usuario = :old.id_usuario;
END;

--INSERTS DE LA TABLA DE HOTELES--
INSERT ALL
INTO tbHoteles (nombre, descripcion, direccion, latitudHotel, longitudHotel, correo, cantidad_habitaciones,img_url ,id_usuario) VALUES ('Royal Decameron Salinitas', 'Este resort todo incluido está ubicado en la costa del Pacífico y es conocido por sus amplias instalaciones recreativas y su ambiente familiar. Royal Decameron Salinitas cuenta con varias piscinas, incluyendo una piscina de agua salada y toboganes acuáticos, así como acceso directo a una playa privada.', 'Carretera al Litoral Km. 84, Acajutla, Sonsonate, El Salvador', 13.532793961041605, -89.81632836143223, 'royaldecameron@gmail.com', 50,'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/2c/1c/cc/d8/hotel-exterior.jpg',1)
INTO tbHoteles (nombre, descripcion, direccion, latitudHotel, longitudHotel, correo, cantidad_habitaciones,img_url , id_usuario) VALUES ('Sheraton Presidente', 'Situado en una zona privilegiada de San Salvador, el Sheraton Presidente es un hotel de cuatro estrellas que combina elegancia clásica con comodidades modernas. Las 225 habitaciones y suites están diseñadas para ofrecer el máximo confort, con ropa de cama de lujo, televisores de pantalla plana, y acceso a Internet. ', 'Avenida La Revolución, Colonia San Benito, San Salvador, El Salvador', 13.691651009852649, -89.24177726328303, 'sheratonpresidente@gmail.com', 50,'https://www.decameron.com/images/destinos/el-salvador/salinitas-002.jpg',2)
INTO tbHoteles (nombre, descripcion, direccion, latitudHotel, longitudHotel, correo, cantidad_habitaciones,img_url , id_usuario) VALUES ('Hotel Oasis', 'El Hotel Oasis es un refugio encantador situado en el corazón de La Libertad, una de las zonas más populares de El Salvador por sus impresionantes playas y vibrante vida local. Este hotel boutique ofrece una mezcla perfecta de confort, tranquilidad y un toque de lujo, ideal para aquellos que buscan una escapada relajante.','San Salvador, en la Colonia Libertad, Avenida Morazán, Pasaje Morelos #111.', 13.724179169106629, -89.20811381667423, 'hoteloasis@gmail.com', 50,'https://cdn.forbes.com.mx/2020/07/hoteles-Grand-Velas-Resorts-e1596047698604.jpg',3)
INTO tbHoteles (nombre, descripcion, direccion, latitudHotel, longitudHotel, correo, cantidad_habitaciones,img_url , id_usuario) VALUES ('Real Intercontinental', 'Ubicado en una de las zonas más exclusivas de San Salvador, el Real InterContinental es sinónimo de lujo y confort. Este hotel de cinco estrellas cuenta con 228 habitaciones equipadas con las últimas tecnologías y comodidades, como camas con colchones pillow-top, baños de mármol, y acceso a Internet de alta velocidad.', 'Boulevard de Los Héroes y Avenida Sisimiles, San Salvador, El Salvador', 13.707657835513112, -89.21298453259253, 'realintercontinental@gmail.com', 50,'https://www.kayak.com.sv/rimg/himg/45/0a/16/expediav2-373051-30d516-883802.jpg',4)
INTO tbHoteles (nombre, descripcion, direccion, latitudHotel, longitudHotel, correo, cantidad_habitaciones,img_url , id_usuario) VALUES ('Las Flores Resort', 'Situado en la hermosa playa Las Flores, este resort es un paraíso para los amantes del surf y la naturaleza. Las Flores Resort ofrece bungalows privados rodeados de exuberante vegetación tropical y con vistas al océano. Las habitaciones están decoradas con materiales locales y cuentan con todas las comodidades modernas. ', 'Playa Las Flores, El Cuco, San Miguel, El Salvador', 13.172930224687272, -88.11823202096556, 'lasfloresresort@gmail.com',30,'https://eventoslatam.com/wordpress/wp-content/uploads/2022/09/marriot-barranquilla-1.jpg',5)
SELECT * FROM dual;

SELECT * FROM tbHoteles;

--PARA PODER BORRAR LA TABLA DE HOTELES--
DROP TABLE tbHoteles CASCADE CONSTRAINTS;
DROP SEQUENCE identity_Hoteles;

-- ===============================================
-----TABLA INTERMEDIA DE VALORACIÓN Y HOTELES----
-- ===============================================
CREATE TABLE tbIntermedia_valoracion_hoteles(
id_intermedia_valoracion_hoteles INT PRIMARY KEY,
id_hoteles INT,
id_valoracion INT,
CONSTRAINT FK_IDHOTELES_TBINTERMEDIAVALORACIONES FOREIGN KEY (id_hoteles) REFERENCES tbHoteles(id_hoteles)ON DELETE CASCADE,
CONSTRAINT FK_IDVALORACION_TBINTERMEDIAHOTELES FOREIGN KEY (id_valoracion) REFERENCES tbValoraciones(id_valoracion)ON DELETE CASCADE
);

--SECUENCIA DE TABLA INTERMEDIA DE VALORACIÓN Y HOTELES--
CREATE SEQUENCE seq_tbIntermedia_valoracion_hoteles
START WITH 1
INCREMENT BY 1;

--TRIGGERS DE TABLA INTERMEDIA DE VALORACIÓN Y HOTELES--
CREATE OR REPLACE TRIGGER urg_before_insert_tbIntermediaValoracionesHoteles
BEFORE INSERT ON tbIntermedia_valoracion_hoteles
FOR EACH ROW
BEGIN
--ASIGNAR EL PRÓXIMO VALOR DE LA SECUENCIA A ID_IMÁGENES--
SELECT seq_tbIntermedia_valoracion_hoteles.NEXTVAL INTO :new.id_intermedia_valoracion_hoteles FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_update_tbIntermedia_valoracion_hoteles
AFTER UPDATE OF id_valoracion ON tbValoraciones
FOR EACH ROW
BEGIN
    UPDATE tbIntermedia_valoracion_hoteles
    SET id_valoracion = :new.id_valoracion
    WHERE id_valoracion = :old.id_valoracion;
END;

CREATE OR REPLACE TRIGGER trg_update_tbIntermediaValoracionHoteles_valoracion_hoteles
AFTER UPDATE OF id_hoteles ON tbHoteles
FOR EACH ROW
BEGIN
    UPDATE tbIntermedia_valoracion_hoteles
    SET id_hoteles = :new.id_hoteles
    WHERE id_hoteles = :old.id_hoteles;
END;

--INSERTS DE TABLA INTERMEDIA DE VALORACIÓN Y HOTELES--
INSERT ALL
INTO tbIntermedia_valoracion_hoteles (id_hoteles, id_valoracion) VALUES (1, 1)
INTO tbIntermedia_valoracion_hoteles (id_hoteles, id_valoracion) VALUES (2, 2)
INTO tbIntermedia_valoracion_hoteles (id_hoteles, id_valoracion) VALUES (3,3)
INTO tbIntermedia_valoracion_hoteles (id_hoteles, id_valoracion) VALUES (4,4)
INTO tbIntermedia_valoracion_hoteles (id_hoteles, id_valoracion) VALUES (5,5)
SELECT * FROM dual;

--PARA PODER BORRAR TABLA INTERMEDIA DE VALORACIÓN Y HOTELES--
DROP TABLE tbIntermedia_valoracion_hoteles CASCADE CONSTRAINTS;
DROP SEQUENCE seq_tbIntermedia_valoracion_hoteles;

-- ===============================================
--TABLA DEL CAROUSEL DE IMÁGENES PARA LOS HOTELES--
-- ===============================================
CREATE TABLE tbImagenes_Hoteles (
id_imagenes INT PRIMARY KEY,
id_hoteles INT,
url_imagen VARCHAR (255) ,
CONSTRAINT fk_id_hoteles_tbHoteles FOREIGN KEY (id_hoteles) REFERENCES tbHoteles (id_hoteles)
);

--SECUENCIA DE TABLA DEL CAROUSEL DE IMÁGENES PARA LOS HOTELES--
CREATE SEQUENCE seq_tbImagenes_Hoteles
START WITH 1
INCREMENT BY 1;

--TRIGGER DE TABLA DEL CAROUSEL DE IMÁGENES PARA LOS HOTELES--
CREATE OR REPLACE TRIGGER urg_before_insert_tbImagenes_Hoteles
BEFORE INSERT ON tbImagenes_Hoteles
FOR EACH ROW
BEGIN
--ASIGNAR EL PRÓXIMO VALOR DE LA SECUENCIA A ID_IMÁGENES--
:NEW.id_imagenes := seq_tbImagenes_Hoteles.NEXTVAL;
END;

--INSERTS DE TABLA DEL CAROUSEL DE IMÁGENES PARA LOS HOTELES--
INSERT ALL
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (1,'https://i0.wp.com/foodandpleasure.com/wp-content/uploads/2020/10/65345792-h1-facb_angular_pool_view_300dpi.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (1,'https://www.decameron.com/images/destinos/el-salvador/salinitas-002.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (1,'https://media.admagazine.com/photos/618a6ae690c4ec9a52ca12cd/master/w_1600%2Cc_limit/61423.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (1,'https://www.kayak.com.sv/rimg/himg/45/0a/16/expediav2-373051-30d516-883802.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (1,'https://img.cronista.com/files/image/307/307135/5ffe2f480d5e8_950_534!.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (1,'https://hips.hearstapps.com/hmg-prod/images/mim-ibiza-nocturna-1623928445.jpg')
SELECT * FROM dual;

INSERT ALL
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (2,'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/2c/15/46/23/pool-at-evening-time.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (2,'https://cache.marriott.com/content/dam/marriott-renditions/SALSI/salsi-exterior-3252-hor-feat.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (2,'https://www.nobbot.com/wp-content/uploads/2016/08/hoteles-w-920x515.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (2,'https://content.skyscnr.com/available/1363661288/1363661288_WxH.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (2,'https://z.cdrst.com/foto/hotel-sf/1524d/granderesp/sheraton-presidente-san-salvador-hotel-general-12341a9c.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (2,'https://images.trvl-media.com/lodging/1000000/10000/8400/8391/bcc02dad.jpg')
SELECT * FROM dual;

INSERT ALL
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (3,'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/21/f5/b7/b6/corner-view-at-hotel.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (3,'https://www.nobbot.com/wp-content/uploads/2016/08/hoteles-w-920x515.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (3,'https://e00-expansion.uecdn.es/assets/multimedia/imagenes/2022/09/02/16621337884122.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (3,'https://images.trvl-media.com/lodging/17000000/16610000/16607000/16606929/fc557c2c.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (3,'https://content.r9cdn.net/rimg/himg/9a/22/37/leonardo-1103824-0004_CR-Veracruz-1-Aereas_0296_O-816247.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (3,'https://www.viajeroselsalvador.com/uploads/5/6/1/0/5610753/7538971_orig.jpg')
SELECT * FROM dual;

INSERT ALL
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (4,'https://upload.wikimedia.org/wikipedia/commons/8/83/Hotel_inter_SV.JPG')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (4,'https://www.kayak.com.sv/rimg/himg/a6/9f/d9/expediav2-174068-86368e14_z-090757.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (4,'https://digital.ihg.com/is/image/ihg/intercontinental-san-salvador-3976073550-original')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (4,'https://e00-expansion.uecdn.es/assets/multimedia/imagenes/2022/09/02/16621337884122.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (4,'https://spaceohrtest.sfo2.digitaloceanspaces.com/assets/img/home/galeria-home/2023/photos/2.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (4,'https://content.r9cdn.net/rimg/himg/9a/22/37/leonardo-1103824-0004_CR-Veracruz-1-Aereas_0296_O-816247.jpg')
SELECT * FROM dual;

INSERT ALL
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (5,'https://www.lasfloresresort.com/files/8914/4789/9142/las-flores-resort-2015-6.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (5,'https://i.ytimg.com/vi/lLgUwi609YE/maxresdefault.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (5,'https://www.lasfloresresort.com/files/8414/4789/9147/las-flores-resort-2015-24.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (5,'https://www.lasfloresresort.com/files/8913/6971/7085/st3.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (5,'https://www.lasfloresresort.com/files/2614/3840/3975/las-flores-resort-facilities-19.jpg')
INTO tbImagenes_Hoteles (id_hoteles, url_imagen) VALUES (5,'https://imgcy.trivago.com/c_limit,d_dummy.jpeg,f_auto,h_600,q_auto,w_600//hotelier-images/21/10/8370ae575f0a25226874e1fe1fce6c07d2cb35501b1edd3ef5e9afcd6583.jpeg')
SELECT * FROM dual;

SELECT * FROM tbImagenes_Hoteles;

--PARA PODER BORRAR TABLA DEL CAROUSEL DE IMÁGENES PARA LOS HOTELES--
DROP TABLE tbImagenes_Hoteles CASCADE CONSTRAINTS;
DROP SEQUENCE seq_tbImagenes_Hoteles;

-- ===============================================
---TABLA INTERMEDIA DE HOTELES Y TIPO HABITACIÓN--
-- ===============================================
CREATE TABLE tbIntermedia_Hoteles_TipoHabitacion(
id_IntermediaTipoHabitacion INT PRIMARY KEY,
id_hoteles INT,
id_tipo_habitacion INT,
CONSTRAINT fk_idhoteles_tbintermediaTipoHabitacion FOREIGN KEY (id_hoteles) REFERENCES tbHoteles(id_hoteles) ON DELETE CASCADE,
CONSTRAINT FKid_tipo_habitacion_IntermediaTipoHabitacion FOREIGN KEY(id_tipo_habitacion) REFERENCES tbTiposHabitaciones(id_tipo_habitacion) ON DELETE CASCADE
);

--SECUENCIA DE TABLA INTERMEDIA DE HOTELES Y TIPO HABITACIÓN--
CREATE SEQUENCE identity_tbIntermediaTipoHabitacion
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGERS DE TABLA INTERMEDIA DE HOTELES Y TIPO HABITACIÓN--
CREATE OR REPLACE TRIGGER trg_update_tbTiposHabitaciones_intermediaTipoHabitacion
AFTER UPDATE OF id_tipo_habitacion ON tbTiposHabitaciones
FOR EACH ROW
BEGIN
    UPDATE tbIntermedia_Hoteles_TipoHabitacion
    SET id_tipo_habitacion = :new.id_tipo_habitacion
    WHERE id_tipo_habitacion = :old.id_tipo_habitacion;
END;

CREATE OR REPLACE TRIGGER trg_update_tbHoteles_intermediaTipoHabitacion
AFTER UPDATE OF id_hoteles ON tbHoteles
FOR EACH ROW
BEGIN
    UPDATE tbIntermedia_Hoteles_TipoHabitacion
    SET id_hoteles = :new.id_hoteles
    WHERE id_hoteles = :old.id_hoteles;
END;

CREATE OR REPLACE TRIGGER trg_before_insert_tbIntermediaTipoHabitacion
BEFORE INSERT ON tbIntermedia_Hoteles_TipoHabitacion
FOR EACH ROW
BEGIN
    SELECT identity_tbIntermediaTipoHabitacion.NEXTVAL INTO :new.id_IntermediaTipoHabitacion FROM dual;
END;

--INSERTS DE TABLA INTERMEDIA DE HOTELES Y TIPO HABITACIÓN--
INSERT ALL
INTO tbIntermedia_Hoteles_TipoHabitacion(id_hoteles, id_tipo_habitacion) values (1, 1)
INTO tbIntermedia_Hoteles_TipoHabitacion(id_hoteles, id_tipo_habitacion) values (2, 2)
INTO tbIntermedia_Hoteles_TipoHabitacion(id_hoteles, id_tipo_habitacion) values (3, 3)
INTO tbIntermedia_Hoteles_TipoHabitacion(id_hoteles, id_tipo_habitacion) values (2, 1)
INTO tbIntermedia_Hoteles_TipoHabitacion(id_hoteles, id_tipo_habitacion) values (3, 1)
SELECT * FROM dual;

SELECT * FROM tbIntermedia_Hoteles_TipoHabitacion;

--PARA PODER BORRAR TABLA INTERMEDIA DE HOTELES Y TIPO HABITACION--
DROP TABLE tbImagenes_Hoteles CASCADE CONSTRAINTS;
DROP SEQUENCE seq_tbImagenes_Hoteles;

-- ===============================================
-----TABLA INTERMEDIA DE HOTELES Y SERVICIOS------
-- ===============================================
CREATE TABLE tbIntermedia_Hoteles_Servicios(
idIntermedia INT PRIMARY KEY,
id_hoteles INT,
id_servicio_hotel INT,
CONSTRAINT fk_idhoteles_tbintermedia FOREIGN KEY (id_hoteles) REFERENCES tbHoteles(id_hoteles) ON DELETE CASCADE,
CONSTRAINT fk_idServicioHotel_tbintermedia FOREIGN KEY (id_servicio_hotel) REFERENCES tbServiciosHotel(id_servicio_hotel) ON DELETE CASCADE
);

--SEUENCIA DE TABLA INTERMEDIA DE HOTELES Y SERVICIOS--
CREATE SEQUENCE identity_tbIntermedia
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGERS TABLA INTERMEDIA DE HOTELES Y SERVICIOS--
CREATE OR REPLACE TRIGGER trg_update_tbServiciosHotel_intermedia
AFTER UPDATE OF id_servicio_hotel ON tbServiciosHotel
FOR EACH ROW
BEGIN
    UPDATE tbIntermedia_Hoteles_Servicios
    SET id_servicio_hotel = :new.id_servicio_hotel
    WHERE id_servicio_hotel = :old.id_servicio_hotel;
END;

CREATE OR REPLACE TRIGGER trg_update_tbHoteles_intermedia
AFTER UPDATE OF id_hoteles ON tbHoteles
FOR EACH ROW
BEGIN
    UPDATE tbIntermedia_Hoteles_Servicios
    SET id_hoteles = :new.id_hoteles
    WHERE id_hoteles = :old.id_hoteles;
END;

CREATE OR REPLACE TRIGGER trg_before_insert_tbIntermedia
BEFORE INSERT ON tbIntermedia_Hoteles_Servicios
FOR EACH ROW
BEGIN
    SELECT identity_tbIntermedia.NEXTVAL INTO :new.idIntermedia FROM dual;
END;

--INSERTS DE TABLA INTERMEDIA DE HOTELES Y SERVICIOS--
INSERT ALL
INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(1, 1)
INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(1, 2)
INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(1, 3)
INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(1, 4)
INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(1, 5)

INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(2, 1)
INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(2, 2)

INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(3, 3)
INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(3, 4)

INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(4, 1)
INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(4, 4)
INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(4, 5)

INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(5, 3)
INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(5, 4)
INTO tbIntermedia_Hoteles_Servicios(id_hoteles, id_servicio_hotel) values(5, 5)
SELECT * FROM dual;

SELECT * FROM tbIntermedia_Hoteles_Servicios;

--PARA PODER BORRAR DE TABLA INTERMEDIA DE HOTELES Y SERVICIOS--
DROP TABLE tbIntermedia_Hoteles_Servicios CASCADE CONSTRAINTS;
DROP SEQUENCE identity_tbIntermedia;

-- ===============================================
--------------TABLA DE HABITACIONES---------------
-- ===============================================
CREATE TABLE tbHabitaciones(
id_habitacion INT PRIMARY KEY,
entrada VARCHAR2(25) NOT NULL,
salida VARCHAR2(25) NOT NULL,
numero_tarjeta VARCHAR2(19) NOT NULL,
CONSTRAINT chk_numero_tarjeta_longitud CHECK (LENGTH(numero_tarjeta) >= 16),
CONSTRAINT chk_numero_tarjeta_formato CHECK (numero_tarjeta NOT LIKE '%[^0-9]%'),
fecha_caducidad_tarjeta VARCHAR2(25) NOT NULL,
nombre_titular_tarjeta VARCHAR2(20) NOT NULL,
CVV INT NOT NULL,
Total NUMBER NOT NULL,
id_tipo_habitacion INT,
id_hoteles INT,
id_departamento INT,
id_usuario INT,
CONSTRAINT chk_fecha CHECK (entrada < salida),
CONSTRAINT FKid_tipo_habitacion_habitaciones FOREIGN KEY(id_tipo_habitacion) REFERENCES tbTiposHabitaciones(id_tipo_habitacion) ON DELETE CASCADE,
CONSTRAINT FKid_hoteles_habitaciones FOREIGN KEY(id_hoteles) REFERENCES tbHoteles(id_hoteles) ON DELETE CASCADE,
CONSTRAINT FKid_departamento_habitaciones FOREIGN KEY(id_departamento) REFERENCES tbDepartamentos(id_departamento) ON DELETE CASCADE,
CONSTRAINT FKid_usuario_habitaciones FOREIGN KEY(id_usuario) REFERENCES tbUsuarios(id_usuario) ON DELETE CASCADE
);

--SECUENCIA DE TABLA DE HABITACIONES--
CREATE SEQUENCE identity_Habitaciones
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGER DE TABLA DE HABITACIONES--
CREATE OR REPLACE TRIGGER trg_before_insert_tbHabitaciones
BEFORE INSERT ON tbHabitaciones
FOR EACH ROW
BEGIN
    SELECT identity_Habitaciones.NEXTVAL INTO :new.id_habitacion FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_update_tbHabitacion_tipo_habitaciones
AFTER UPDATE OF id_tipo_habitacion ON tbTiposHabitaciones
FOR EACH ROW
BEGIN
    UPDATE tbHabitaciones
    SET id_tipo_habitacion = :new.id_tipo_habitacion
    WHERE id_tipo_habitacion = :old.id_tipo_habitacion;
END;

CREATE OR REPLACE TRIGGER trg_update_tbHabitacion_departamentos
AFTER UPDATE OF id_departamento ON tbDepartamentos
FOR EACH ROW
BEGIN
    UPDATE tbHabitaciones
    SET id_departamento = :new.id_departamento
    WHERE id_departamento = :old.id_departamento;
END;

--INSERTS DE LA TABLA DE HABITACIONES--
INSERT ALL
INTO tbHabitaciones (id_hoteles, entrada, salida, numero_tarjeta, fecha_caducidad_tarjeta, nombre_titular_tarjeta, CVV, Total, id_tipo_habitacion, id_departamento, id_usuario) VALUES (1,  '2024-06-01', '2024-06-10', '1234567812345678', '2026-06-01', 'Juan Perez', 123, 200, 1, 1, 1)
INTO tbHabitaciones (id_hoteles, entrada, salida, numero_tarjeta, fecha_caducidad_tarjeta, nombre_titular_tarjeta, CVV, Total, id_tipo_habitacion, id_departamento, id_usuario) VALUES (2,  '2024-06-05', '2024-06-15', '2345678923456789', '2027-06-01', 'Maria Lopez', 456, 100, 2, 2, 2)
INTO tbHabitaciones (id_hoteles, entrada, salida, numero_tarjeta, fecha_caducidad_tarjeta, nombre_titular_tarjeta, CVV, Total, id_tipo_habitacion, id_departamento, id_usuario) VALUES (3,  '2024-06-10',  '2024-06-20',  '3456789034567890', '2028-06-01',  'Luis Martinez', 789, 350, 3, 3, 3)
INTO tbHabitaciones (id_hoteles, entrada, salida, numero_tarjeta, fecha_caducidad_tarjeta, nombre_titular_tarjeta, CVV, Total, id_tipo_habitacion, id_departamento, id_usuario) VALUES (4,  '2024-07-01',  '2024-07-10',  '4567890145678901', '2029-07-01', 'Ana Gomez', 101, 200, 4, 4, 4)
INTO tbHabitaciones (id_hoteles, entrada, salida, numero_tarjeta, fecha_caducidad_tarjeta, nombre_titular_tarjeta, CVV, Total, id_tipo_habitacion, id_departamento, id_usuario) VALUES (5,  '2024-08-01', '2024-08-10',  '5678901256789012', '2030-08-01', 'Carlos Ramirez', 202, 100, 5 ,5, 5)
SELECT * FROM dual;

SELECT * FROM tbHabitaciones;

--PARA PODER BORRAR DE TABLA HABITACIONES--
DROP TABLE tbHabitaciones CASCADE CONSTRAINTS;
DROP SEQUENCE identity_Habitaciones;

-- ===============================================
-----------------TABLA DE ESTADO------------------
-- ===============================================
CREATE TABLE tbEstado(
id_estado INT PRIMARY KEY,
tipo_estado VARCHAR2(20) NOT NULL
);

--SECUENCIA DE LA TABLA DE ESTADOS--
CREATE SEQUENCE identity_Estado
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGER DE LA TABLA DE ESTADOS--
CREATE OR REPLACE TRIGGER trg_before_insert_tbEstado
BEFORE INSERT ON tbEstado
FOR EACH ROW
BEGIN
    SELECT identity_Estado.NEXTVAL INTO :new.id_estado FROM dual;
END;

--INSERTS DE LA TABLA DE ESTADOS
INSERT ALL
INTO tbEstado (tipo_estado) VALUES ('Disponible')
INTO tbEstado (tipo_estado) VALUES ('No disponible')
SELECT * FROM dual;

SELECT * FROM tbEstado;

--PARA PODER BORRAR DE ESTADOS--
DROP TABLE tbEstado CASCADE CONSTRAINTS;
DROP SEQUENCE identity_Estado;

-- ===============================================
-----------------TABLA DE RESERVAS----------------
-- ===============================================
CREATE TABLE tbReservas(
id_reserva INT PRIMARY KEY,
id_usuario INT,
id_habitacion INT,
id_estado INT,
CONSTRAINT FKid_estado_reservas FOREIGN KEY(id_estado) REFERENCES tbEstado(id_estado) ON DELETE CASCADE,
CONSTRAINT FKid_usuario_reservas FOREIGN KEY(id_usuario) REFERENCES tbUsuarios(id_usuario) ON DELETE CASCADE,
CONSTRAINT FKid_habitacion_reservas FOREIGN KEY(id_habitacion) REFERENCES tbHabitaciones(id_habitacion) ON DELETE CASCADE
);

--SECUENCIA DE LA TABLA DE RESERVAS--
CREATE SEQUENCE identity_Reservas
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGERS DE LA TABLA DE RESERVAS--
CREATE OR REPLACE TRIGGER trg_update_tbReservas_estado
AFTER UPDATE OF id_estado ON tbEstado
FOR EACH ROW
BEGIN
    UPDATE tbReservas
    SET id_estado = :new.id_estado
    WHERE id_estado = :old.id_estado;
END;

CREATE OR REPLACE TRIGGER trg_update_tbReservas_usuario
AFTER UPDATE OF id_usuario ON tbUsuarios
FOR EACH ROW
BEGIN
    UPDATE tbReservas
    SET id_usuario = :new.id_usuario
    WHERE id_usuario = :old.id_usuario;
END;

CREATE OR REPLACE TRIGGER trg_update_tbReservas_habitacion
AFTER UPDATE OF id_habitacion ON tbHabitaciones
FOR EACH ROW
BEGIN
    UPDATE tbReservas
    SET id_habitacion = :new.id_habitacion
    WHERE id_habitacion = :old.id_habitacion;
END;

CREATE OR REPLACE TRIGGER trg_before_insert_tbReservas
BEFORE INSERT ON tbReservas
FOR EACH ROW
BEGIN
    SELECT identity_Reservas.NEXTVAL INTO :new.id_reserva FROM dual;
END;

--INSERTS DE LA TABLA DE RESERVAS--
INSERT ALL
INTO tbReservas(id_usuario, id_habitacion, id_estado) VALUES (1, 1, 1)
INTO tbReservas(id_usuario, id_habitacion, id_estado) VALUES (2, 2, 1)
INTO tbReservas(id_usuario, id_habitacion, id_estado) VALUES (3, 3, 2)
INTO tbReservas(id_usuario, id_habitacion, id_estado) VALUES (4, 4, 2)
INTO tbReservas(id_usuario, id_habitacion, id_estado) VALUES (5, 5, 1)
SELECT * FROM dual;

SELECT * FROM tbReservas;

--PARA PODER BORRAR DE RESERVAS--
DROP TABLE tbReservas CASCADE CONSTRAINTS;
DROP SEQUENCE identity_Reservas;

-- ===============================================
----------------TABLA DE HISTORIAL----------------
-- ===============================================
CREATE TABLE tbHistoriales(
id_historial INT PRIMARY KEY,
id_reserva INT,
id_hoteles INT,
CONSTRAINT FKid_hoteles_historial FOREIGN KEY(id_hoteles) REFERENCES tbHoteles(id_hoteles) ON DELETE CASCADE,
CONSTRAINT FKid_reservas2_historial  FOREIGN KEY(id_reserva) REFERENCES tbReservas(id_reserva) ON DELETE CASCADE
);

--SECUENCIA DE LA TABLA DE HISTORIAL--
CREATE SEQUENCE identity_Historiales
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGERS DE LA TABLA DE HISTORIAL--
CREATE OR REPLACE TRIGGER trg_before_insert_tbHistoriales
BEFORE INSERT ON tbHistoriales
FOR EACH ROW
BEGIN
    SELECT identity_Historiales.NEXTVAL INTO :new.id_historial FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_update_tbHistorial_reserva
AFTER UPDATE OF id_reserva ON tbReservas
FOR EACH ROW
BEGIN
    UPDATE tbHistoriales
    SET id_reserva = :new.id_reserva
    WHERE id_reserva = :old.id_reserva;
END;

CREATE OR REPLACE TRIGGER trg_update_tbHistorial_hoteles
AFTER UPDATE OF id_hoteles ON tbHoteles
FOR EACH ROW
BEGIN
    UPDATE tbHistoriales
    SET id_hoteles = :new.id_hoteles
    WHERE id_hoteles = :old.id_hoteles;
END;

--INSERTS DE LA TABLA DE HISTORIAL--
INSERT ALL
INTO tbHistoriales(id_reserva, id_hoteles) VALUES (1, 1)
INTO tbHistoriales(id_reserva, id_hoteles) VALUES (2, 2)
INTO tbHistoriales(id_reserva, id_hoteles) VALUES (3, 3)
INTO tbHistoriales(id_reserva, id_hoteles) VALUES (4, 4)
INTO tbHistoriales(id_reserva, id_hoteles) VALUES (5, 5)
SELECT * FROM dual;

SELECT * FROM tbHistoriales;

--PARA PODER BORRAR DE HISTORIAL--
DROP TABLE tbHistoriales CASCADE CONSTRAINTS;
DROP SEQUENCE identity_Historiales;

-- ===============================================
-------------TABLA DE PREFERENCIALES--------------
-- ===============================================
CREATE TABLE tbPreferenciales(
id_preferencial INT PRIMARY KEY,
id_hoteles INT,
id_usuario INT,
CONSTRAINT FKid_hoteles2_preferenciales FOREIGN KEY(id_hoteles)REFERENCES tbHoteles(id_hoteles)ON DELETE CASCADE,
CONSTRAINT FKid_usuario_preferenciales FOREIGN KEY(id_usuario)REFERENCES tbUsuarios(id_usuario)ON DELETE CASCADE
);

--SECUENCIA DE LA TABLA DE PREFERENCIALES--
CREATE SEQUENCE identity_Preferenciales
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGERS DE LA TABLA DE PREFERENCIALES--
CREATE OR REPLACE TRIGGER trg_before_insert_tbPreferenciales
BEFORE INSERT ON tbPreferenciales
FOR EACH ROW
BEGIN
    SELECT identity_Preferenciales.NEXTVAL INTO :new.id_preferencial FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_update_tbPreferenciales_hoteles
AFTER UPDATE OF id_hoteles ON tbHoteles
FOR EACH ROW
BEGIN
    UPDATE tbPreferenciales
    SET id_hoteles = :new.id_hoteles
    WHERE id_hoteles = :old.id_hoteles;
END;

--INSERTS DE LA TABLA DE PREFERENCIALES--
INSERT ALL
INTO tbPreferenciales(id_hoteles, id_usuario) VALUES (1, 1)
INTO tbPreferenciales(id_hoteles, id_usuario) VALUES (2, 2)
INTO tbPreferenciales(id_hoteles, id_usuario) VALUES (3, 3)
INTO tbPreferenciales(id_hoteles, id_usuario) VALUES (4, 4)
INTO tbPreferenciales(id_hoteles, id_usuario) VALUES (5, 5)
SELECT * FROM dual;

SELECT * FROM tbPreferenciales;

--PARA PODER BORRAR DE PREFERENCIALES--
DROP TABLE tbPreferenciales CASCADE CONSTRAINTS;
DROP SEQUENCE identity_Preferenciales;

-- ===============================================
----------------TABLA DE DENUNCIAS----------------
-- ===============================================
CREATE TABLE tbDenuncias(
id_denuncia INT PRIMARY KEY,
nombre_denuncia VARCHAR2(150) NOT NULL,
id_hoteles INT,
CONSTRAINT FKid_hoteles_denuncias FOREIGN KEY(id_hoteles) REFERENCES tbHoteles(id_hoteles) ON DELETE CASCADE
);

--SECUENCIA DE LA TABLA DE DENUNCIAS--
CREATE SEQUENCE identity_Denuncias
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGERS DE LA TABLA DE DENUNCIAS--
CREATE OR REPLACE TRIGGER trg_before_insert_tbDenuncias
BEFORE INSERT ON tbDenuncias
FOR EACH ROW
BEGIN
    SELECT identity_Denuncias.NEXTVAL INTO :new.id_denuncia FROM dual;
END;

CREATE OR REPLACE TRIGGER trg_update_tbDenuncias_hoteles
AFTER UPDATE OF id_hoteles ON tbHoteles
FOR EACH ROW
BEGIN
    UPDATE tbDenuncias
    SET id_hoteles = :new.id_hoteles
    WHERE id_hoteles = :old.id_hoteles;
END;

--INSERTS DE LA TABLA DE DENUNCIAS--
INSERT ALL
INTO tbDenuncias(nombre_denuncia, id_hoteles) VALUES ('El hotel no existe', 1)
INTO tbDenuncias(nombre_denuncia, id_hoteles) VALUES ('Cobran más de lo que dice', 2)
INTO tbDenuncias(nombre_denuncia, id_hoteles) VALUES ('El hotel discrimina a las personas', 3)
INTO tbDenuncias(nombre_denuncia, id_hoteles) VALUES ('El personal es grosero', 4)
INTO tbDenuncias(nombre_denuncia, id_hoteles) VALUES ('Las instalaciones son desagradables', 5)
SELECT * FROM dual;

SELECT * FROM tbDenuncias;

--PARA PODER BORRAR DE DENUNCIAS--
DROP TABLE tbDenuncias CASCADE CONSTRAINTS;
DROP SEQUENCE identity_Denuncias;

-- ===============================================
-----------------TABLA DE OFERTAS-----------------
-- ===============================================
CREATE TABLE tbOfertas(
id_oferta INT PRIMARY KEY,
nombre_oferta VARCHAR2(500) NOT NULL,
descuentoTotal NUMBER NOT NULL,
id_hoteles INT,
CONSTRAINT FK_tbOfertas_idhoteles FOREIGN KEY(id_hoteles) REFERENCES tbHoteles(id_hoteles) ON DELETE CASCADE
);

--SECUENCIA DE LA TABLA DE OFERTAS--
CREATE SEQUENCE identity_Ofertas
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGER DE LA TABLA DE OFERTAS--
CREATE OR REPLACE TRIGGER trg_before_insert_tbOfertas
BEFORE INSERT ON tbOfertas
FOR EACH ROW
BEGIN
    SELECT identity_Ofertas.NEXTVAL INTO :new.id_oferta FROM dual;
END;

--INSERTS DE LA TABLA DE OFERTAS--
INSERT ALL
INTO tbOfertas (nombre_oferta, descuentoTotal,  id_hoteles) VALUES ('3 noches en cabaña ecológica con excursiones a la selva y clases de yoga. ',30, 1)
INTO tbOfertas (nombre_oferta, descuentoTotal, id_hoteles) VALUES ('Dos noches en suite de lujo con desayuno continental y botella de vino.',50, 2)
INTO tbOfertas (nombre_oferta, descuentoTotal, id_hoteles) VALUES ('Escapada romántica con cena bajo las estrellas y suite con jacuzzi privado. ',25, 3)
INTO tbOfertas (nombre_oferta, descuentoTotal, id_hoteles) VALUES ('Estancia familiar de 5 noches con actividades para niños y buffet ilimitado. ',15, 1)
INTO tbOfertas (nombre_oferta, descuentoTotal, id_hoteles) VALUES ('Disfruta de 3 noches al precio de 2 con desayuno incluido y acceso al spa. ',40, 2)
SELECT * FROM dual;

SELECT * FROM tbOfertas;

--PARA PODER BORRAR DE OFERTAS--
DROP TABLE tbOfertas CASCADE CONSTRAINTS;
DROP SEQUENCE identity_Ofertas;

-- ===============================================
-----------------TABLA DE FILTROS-----------------
-- ===============================================
CREATE TABLE tbFiltros (
    id_filtro INT PRIMARY KEY,
    id_hoteles INT,
    id_tipo_habitacion INT,
    id_servicio_hotel INT,
    id_servicio_habitacion INT,
    FOREIGN KEY (id_hoteles) REFERENCES tbHoteles(id_hoteles),
    FOREIGN KEY (id_tipo_habitacion) REFERENCES tbTiposHabitaciones(id_tipo_habitacion),
    FOREIGN KEY (id_servicio_hotel) REFERENCES tbServiciosHotel(id_servicio_hotel),
    FOREIGN KEY (id_servicio_habitacion) REFERENCES tbServiciosHabitacion(id_servicio_habitacion)
);

--INSERTS DE LA TABLA DE FILTROS--
INSERT INTO tbFiltros (id_filtro, id_hoteles, id_tipo_habitacion, id_servicio_hotel, id_servicio_habitacion)
VALUES (1, 1, 1, 1, 1);

INSERT INTO tbFiltros (id_filtro, id_hoteles, id_tipo_habitacion, id_servicio_hotel, id_servicio_habitacion)
VALUES (2, 2, 2, 2, 2);

INSERT INTO tbFiltros (id_filtro, id_hoteles, id_tipo_habitacion, id_servicio_hotel, id_servicio_habitacion)
VALUES (3, 3, 3, 3, 3);

INSERT INTO tbFiltros (id_filtro, id_hoteles, id_tipo_habitacion, id_servicio_hotel, id_servicio_habitacion)
VALUES (4, 4, 4, 4, 4);

INSERT INTO tbFiltros (id_filtro, id_hoteles, id_tipo_habitacion, id_servicio_hotel, id_servicio_habitacion)
VALUES (5, 5, 5, 5, 5);

SELECT * FROM tbFiltros;

--PARA PODER BORRAR DE FILTROS--
DROP TABLE tbFiltros CASCADE CONSTRAINTS;
-- =============================================================================================================================================================
--TABLA DE AUDITORIA PARA GUARDAR UN REGISTRO AL MOMENTO DE ELIMINAR HOTELES, SE GUARDA EL NOMBRE DEL HOTEL, NOMBRE DEL USUARIO Y LA FECHA EN QUE SE ELIMINO--
-- =============================================================================================================================================================
CREATE TABLE tbAuditoriaHoteles (
    clave INT PRIMARY KEY,                      
    nombreHotel VARCHAR2(200) NOT NULL,          
    correoUsuario VARCHAR2(100) NOT NULL,      
    correoHotel VARCHAR2(100) NOT NULL,          
    fecha DATE NOT NULL                        
);

--SECUENCIA DE LA TABLA AUDITORIA--
CREATE SEQUENCE identity_AuditoriaHoteles
START WITH 1
INCREMENT BY 1
NOCACHE;

--TRIGGER DE LA TABLA AUDITORIA--
CREATE OR REPLACE TRIGGER Trigger_Auditoria_Hoteles
BEFORE DELETE ON tbHoteles
FOR EACH ROW
DECLARE
    var_correoUsuario tbUsuarios.correo%TYPE; -- Correo del usuario
    var_nombreHotel tbHoteles.nombre%TYPE;     -- Nombre del hotel
    var_correoHotel tbHoteles.correo%TYPE;     -- Correo del hotel
    var_fecha DATE;                             -- Fecha de la operaci n
BEGIN
    -- ASIGNAR VALORES A LAS VARIABLES
    var_nombreHotel := :OLD.nombre;             -- Nombre del hotel que se est  eliminando
    var_correoHotel := :OLD.correo;             -- Correo del hotel
    var_fecha := SYSDATE;                       -- Fecha actual

    -- OBTENER EL CORREO DEL USUARIO RELACIONADO CON EL HOTEL
    SELECT u.correo
    INTO var_correoUsuario
    FROM tbUsuarios u
    WHERE u.id_usuario = :OLD.id_usuario;       -- Usa el id_usuario del hotel que se est  eliminando

    -- INSERTAR EN LA TABLA AUDITORIA
    INSERT INTO tbAuditoriaHoteles (clave, nombreHotel, correoUsuario, correoHotel, fecha)
    VALUES (
        identity_AuditoriaHoteles.NEXTVAL,       --Secuencia autoincremento
        var_nombreHotel,                         -- Nombre del hotel
        var_correoUsuario,                       -- Correo del usuario
        var_correoHotel,                         -- Correo del hotel
        var_fecha                                -- Fecha del sistema
    );
--Finaliza el trigger
END Trigger_Auditoria_Hoteles;

SELECT * from tbAuditoriaHoteles;
SELECT * from tbHoteles;
DELETE FROM tbHoteles WHERE id_hoteles = 1;

--PARA PODER BORRAR LA TABLA AUDITORIA--
DROP TABLE tbAuditoriaHoteles CASCADE CONSTRAINTS;
DROP SEQUENCE identity_AuditoriaHoteles;

-- ===============================================
-----------PROCEDIMIENTO ALMACENADO---------------
-- ===============================================
CREATE OR REPLACE PROCEDURE ACTUALIZAR_HOTELES
(
    hotel_id_hoteles IN tbHoteles.id_hoteles%TYPE,
    hotel_nombre IN tbHoteles.nombre%TYPE,
    hotel_descripcion IN tbHoteles.descripcion%TYPE,
    hotel_direccion IN tbHoteles.direccion%TYPE,
    hotel_correo IN tbHoteles.correo%TYPE,
    hotel_cantidad_habitaciones IN tbHoteles.cantidad_habitaciones%TYPE,
    hotel_img_url IN tbHoteles.img_url%TYPE,
    hotel_id_usuario IN tbHoteles.id_usuario%TYPE
)
AS
BEGIN
    UPDATE tbHoteles SET
    nombre = hotel_nombre,
    descripcion = hotel_descripcion,
    direccion = hotel_direccion,
    correo = hotel_correo,
    cantidad_habitaciones = hotel_cantidad_habitaciones,
    img_url = hotel_img_url,
    id_usuario = hotel_id_usuario
    WHERE id_hoteles = hotel_id_hoteles;
END;

BEGIN
ACTUALIZAR_HOTELES(1, 'Royal Decameron Salinitas', 'Este resort todo incluido está ubicado en la costa del Pacífico y es conocido por sus amplias instalaciones recreativas y su ambiente familiar. Royal Decameron Salinitas cuenta con varias piscinas, incluyendo una piscina de agua salada y toboganes acuáticos, así como acceso directo a una playa privada.', 'Escalon', 'reservas@decameron.com', 50,'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/2c/1c/cc/d8/hotel-exterior.jpg',1);
END;

Select * from tbHoteles;

-- ===============================================
-----------PROCEDIMIENTO ALMACENADO---------------
-- ===============================================
CREATE OR REPLACE PROCEDURE ACTUALIZAR_HOTELES
(
    hotel_id_hoteles IN tbHoteles.id_hoteles%TYPE,
    hotel_nombre IN tbHoteles.nombre%TYPE,
    hotel_descripcion IN tbHoteles.descripcion%TYPE,
    hotel_direccion IN tbHoteles.direccion%TYPE,
    hotel_correo IN tbHoteles.correo%TYPE,
    hotel_cantidad_habitaciones IN tbHoteles.cantidad_habitaciones%TYPE,
    hotel_img_url IN tbHoteles.img_url%TYPE,
    hotel_id_usuario IN tbHoteles.id_usuario%TYPE
)
AS
BEGIN
    UPDATE tbHoteles SET
    nombre = hotel_nombre,
    descripcion = hotel_descripcion,
    direccion = hotel_direccion,
    correo = hotel_correo,
    cantidad_habitaciones = hotel_cantidad_habitaciones,
    img_url = hotel_img_url,
    id_usuario = hotel_id_usuario
    WHERE id_hoteles = hotel_id_hoteles;
END;

BEGIN
ACTUALIZAR_HOTELES(1, 'Royal Decameron Salinitas', 'Este resort todo incluido está ubicado en la costa del Pacífico y es conocido por sus amplias instalaciones recreativas y su ambiente familiar. Royal Decameron Salinitas cuenta con varias piscinas, incluyendo una piscina de agua salada y toboganes acuáticos, así como acceso directo a una playa privada.', 'Escalon', 'reservas@decameron.com', 50,'https://dynamic-media-cdn.tripadvisor.com/media/photo-o/2c/1c/cc/d8/hotel-exterior.jpg',1);
END;

SELECT * FROM tbHoteles;

-- ===============================================
-----------COMBINACIONES INTERNAS-----------------
-- ===============================================

--INNER JOIN TBUSUARIOS--
SELECT u.id_usuario, u.nombre_usuario, u.apellido, u.fecha_nacimiento, u.correo, u.telefono, u.contraseña, tu.nombre_usuario
FROM tbUsuarios u
INNER JOIN tbTiposUsuarios tu ON u.id_tipo_usuario = tu.id_tipo_usuario;

--INNER JOIN TBVALORACIONES--
SELECT v.id_valoracion, v.comentario, nu.nombre_usuario, nc.nombre_calificación
FROM tbValoraciones v
INNER JOIN tbUsuarios nu ON v.id_usuario = nu.id_usuario
INNER JOIN tbCalificación nc ON v.id_calificación = nc.id_calificación;

--INNER JOIN TBHABITACIONES--
SELECT h.id_habitacion, h.entrada, h.salida,  h.numero_tarjeta,  h.fecha_caducidad_tarjeta, h.nombre_titular_tarjeta, h.cvv, th.nombre_tipo_habitacion, nh.nombre,  nd.nombre_departamento, nu.nombre_usuario
FROM tbHabitaciones h
INNER JOIN tbTiposHabitaciones th ON h.id_tipo_habitacion = th.id_tipo_habitacion
INNER JOIN tbHoteles nh ON h.id_hoteles = nh.id_hoteles
INNER JOIN tbDepartamentos nd ON  h.id_departamento = nd.id_departamento
INNER JOIN tbUsuarios nu ON h.id_usuario = nu.id_usuario;

--INNER JOIN TBRESERVAS--
SELECT u.nombre_usuario AS nombre_usuario,
       th.nombre_tipo_habitacion AS nombre_tipo_habitacion,
       e.tipo_estado AS tipo_estado
FROM tbReservas r
INNER JOIN tbUsuarios u ON r.id_usuario = u.id_usuario
INNER JOIN tbHabitaciones h ON r.id_habitacion = h.id_habitacion
INNER JOIN tbTiposHabitaciones th ON h.id_tipo_habitacion = th.id_tipo_habitacion
INNER JOIN tbEstado e ON r.id_estado = e.id_estado;

--INNER JOIN TBOFERTAS--
SELECT tof.nombre_oferta, th.nombre, tof.descuentoTotal, th.id_hoteles
FROM tbOfertas tof
INNER JOIN tbHoteles th ON tof.id_hoteles = th.id_hoteles;

--INNER JOIN TBDENUNCIAS--
SELECT de.id_denuncia, de.nombre_denuncia, nh.nombre
FROM tbDenuncias de
INNER JOIN tbHoteles nh ON de.id_hoteles = nh.id_hoteles;

--INNER JOIN TBHISTORIALES--
SELECT h.id_historial, u.nombre_usuario AS nombre_usuario, ht.nombre AS nombre_hotel
FROM tbHistoriales h
INNER JOIN tbReservas r ON h.id_reserva = r.id_reserva
INNER JOIN tbUsuarios u ON r.id_usuario = u.id_usuario
INNER JOIN tbHoteles ht ON h.id_hoteles = ht.id_hoteles;

--INNER TBPREFERENCIALES--
SELECT hot.nombre, usu.nombre_usuario
FROM tbPreferenciales prf
INNER JOIN tbHoteles hot ON prf.id_hoteles = hot.id_hoteles
INNER JOIN tbUsuarios usu ON prf.id_usuario = usu.id_usuario;

--INNER JOIN TBINTERMEDIA_VALORACIÓN_HOTELES--  
SELECT nom.nombre, co.comentario
FROM tbIntermedia_valoracion_hoteles ivh
INNER JOIN tbHoteles nom ON ivh.id_hoteles = nom.id_hoteles
INNER JOIN tbValoraciones co ON ivh.id_valoracion = co.id_valoracion;

--INNER JOIN TBINTERMEDIA_HOTELES_TIPOHABITACION--
SELECT nh.nombre, nth.nombre_tipo_habitacion
FROM tbIntermedia_Hoteles_TipoHabitacion iht
INNER JOIN tbHoteles nh ON iht.id_hoteles = nh.id_hoteles
INNER JOIN tbTiposHabitaciones nth ON iht.id_tipo_habitacion = nth.id_tipo_habitacion;

--INNER JOIN TBINTERMEDIA_HOTELES_SERVICIO--
SELECT nom. nombre, ns.nombre_servicio
FROM  tbIntermedia_Hoteles_Servicios ihs
INNER JOIN tbHoteles nom ON ihs.id_hoteles = nom.id_hoteles
INNER JOIN tbServiciosHotel ns ON ihs.id_servicio_hotel  = ns.id_servicio_hotel;

--INNER JOIN TBHOTELES Y TBIMÁGENES_HOTELES--
SELECT i.*
FROM tbHoteles h
INNER JOIN tbImagenes_Hoteles i ON h.id_hoteles = i.id_hoteles;
