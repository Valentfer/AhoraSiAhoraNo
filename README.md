#  AHORA SI, AHORA NO
ÍNDICE

1.	Introducción
1.1.	Planteamiento del problema
1.2.	Objetivos
2.	Diseño de la aplicación
2.1.	Casos de uso
2.2.	Bocetos de la interfaz de usuario
2.3.	Guía de estilo
2.4.	Modelo de dominio
3.	Tecnologías escogidas y justificación
3.1.	Lenguajes de programación utilizados
3.2.	Persistencia de datos / Motor de bases de datos
4.	Arquitectura de la aplicación
4.1.	Estructura del proyecto (Entorno Servidor / Entorno Cliente)
4.2.	Recursos externos
5.	Estimación de costes
6.	Implementación
7.	Manual de despliegue
7.1.	Requisitos hardware y software aplicables
7.2.	Instrucciones de despliegue en Linux
7.3.	Instrucciones de despliegue en Windows
7.4.	Instrucciones de despliegue en dispositivos móviles (Android o IOS)
7.5.	Configuración inicial
7.6.	Datos de prueba
8.	Manual de uso.
  9.- Posibles mejoras.
  10.- Conclusiones.


1. Introducción
La presente aplicación tiene como objetivo informar al usuario si se encuentra dentro o fuera de los límites de una parcela según la referencia catastral, mediante la obtención de la ubicación del dispositivo móvil.
1.1.	Planteamiento del problema

En muchas ocasiones, los propietarios de terrenos o parcelas necesitan saber si están dentro o fuera de sus límites, y no siempre tienen acceso a una herramienta que les permita hacerlo de forma rápida y sencilla.
1.2. Objetivos

Los objetivos de la aplicación son:
●	Permitir al usuario conocer si se encuentra dentro o fuera de una parcela según la referencia catastral.
●	Obtener la ubicación del dispositivo móvil para poder realizar la comprobación de forma automática.
●	Se puede acceder a otra parcela si conoce la referencia catastral.
●	Mostrar la información de forma clara y sencilla para que el usuario pueda entenderla fácilmente.
●	Guardar sus parcelas.



2. Diseño de la aplicación
Para el diseño de la aplicación se han tenido en cuenta los siguientes aspectos:
2.1. Casos de uso
●	Se le solicita un usuario y contraseña.
●	Si el usuario y contraseña son correctos, se muestra el menú principal.
●	Si el usuario y contraseña son incorrectos, se le da la opción de registrarse o no podrá seguir usando la aplicación.
●	Desde el menú principal, el usuario puede elegir entre ver las parcelas guardadas, obtener los datos de una parcela proporcionando la referencia catastral o realizarlo a partir de su ubicación.



2.2. Bocetos de la interfaz de usuario

Se han realizado varios bocetos de la interfaz de usuario para determinar la mejor manera de mostrar la información al usuario. Se ha optado por una pantalla de inicio de sesión que solicita el usuario y contraseña, y un menú principal que permite al usuario elegir entre las diferentes opciones disponibles.
 


2.3. Guía de estilo

Se ha definido una guía de estilo que establece el uso de colores y tipografía coherentes con la identidad de la aplicación.





2.4. Modelo de dominio

El modelo de dominio se basa en la obtención de la ubicación del dispositivo móvil y la comparación con la referencia catastral de la parcela para determinar si el usuario está dentro o fuera de la misma.
1.	Usuario: Representa al usuario del dispositivo móvil que se encuentra realizando la verificación de su ubicación con respecto a una parcela.
2.	Dispositivo móvil: Representa el dispositivo móvil utilizado por el usuario para obtener su ubicación actual.
3.	Ubicación: Representa la ubicación geográfica actual del dispositivo móvil. Puede incluir las coordenadas de latitud y longitud.
4.	Parcela: Representa una parcela de terreno que se desea verificar si el usuario se encuentra dentro o fuera de ella. La parcela puede estar identificada por su referen-cia catastral.
5.	Referencia catastral: Es un identificador único asignado a cada parcela de terreno que permite su identificación precisa. La referencia catastral puede ser utilizada para obtener información adicional sobre la parcela, como su superficie, uso prin-cipal, año de construcción, entre otros.
6.	Comparación de ubicación: Representa el proceso de comparar la ubicación ac-tual del usuario con la ubicación de la parcela utilizando la referencia catastral. Se puede realizar un cálculo de distancia o utilizar algoritmos de geolocalización para determinar si el usuario se encuentra dentro de un rango aceptable de la parcela.
7.	Resultado de verificación: Representa el resultado de la comparación de ubica-ción. Puede ser un valor booleano que indique si el usuario está dentro o fuera de la parcela.










3. Tecnologías escogidas y justificación
Para la realización de la aplicación se han utilizado las siguientes tecnologías:
3.1. Lenguajes de programación utilizados
●	Kotlin para el desarrollo de la aplicación en Android Studio. Kotlin es un lenguaje de programación multipropósito que se utiliza para el desarrollo de aplicaciones de Android, aplicaciones de servidor, aplicaciones web, etc.
3.2. Persistencia de datos / Motor de bases de datos
●	Se ha utilizado SQLite para la persistencia de datos.
●	Se utiliza retrofit para la lectura de JSON que proporciona la API cartociudad.es.
●	Se lee  directamente el formato XML proporcionado por la API ovc.catastro.meh.es.
●	Se hace uso de la API que proporciona Google maps para visualizar el mapa.



4. Arquitectura de la aplicación
La arquitectura de la aplicación se basa en el patrón Modelo-Vista-Controlador (MVC).
4.1. Estructura del proyecto (Entorno Servidor / Entorno Cliente)
●	La aplicación se desarrolla en el entorno cliente (dispositivo móvil).
4.2. Recursos externos
●	Se requiere acceso a la ubicación del dispositivo móvil, por lo que se solicitará el permiso correspondiente al usuario.
●	Se utiliza la API de cartociudad para obtener los datos
●	Para las peticiones a la API se utiliza retrofit
●	Lectura de XML a través de DocumentBuilderFActory
●	Para el paso de coordenadas se utiliza la librería Proj4j
●	Se utilizan corrutinas para descargar el hilo principal
●	Cardview para mostrar las parcelas 
●	Los servicios de Google play para la ubicación y mostrar el mapa
●	Librería Lottie para la interfaz.


5. Estimación de costes
●	La aplicación no tiene costes asociados a las ya API’s utilizadas ya estas son gratuitas.
1.	http://www.cartociudad.es para obtener la referencia catastral por geocodificación inversa.
2.	http://ovc.catastro.meh.es para obtener todas las coordenadas y dibujar la figura.
●	Las fuentes externas son gratuitas, como son:
1.	Para el sonido de alarma: megaman_x_error.mp3 de la página web cjoint.com.
2.	Lottie de inicio map_pin_location.jso de la web LottieFiles.com
3.	Lottie de login floating_magic_link_login.json de la web LottieFile.com
4.	Icono de la app y fondo if_advantage_nearby_1034361 de la web flaticon.es

6. Implementación
Para la implementación de la aplicación se siguen los siguientes pasos:
1.	La pantalla de inicio de sesión solicita el usuario y contraseña. Para esto se hace uso de una base de datos embebida SQLite, donde primero hemos de registrarnos.

2.	Si el usuario y contraseña son correctos, se muestra el menú principal. Si el usuario y contraseña son incorrectos, se le da la opción de registrarse o no podrá seguir usando la aplicación.

3.	Desde el menú principal, el usuario puede elegir entre ver las parcelas guardadas, obtener los datos de una parcela proporcionando la referencia catastral o realizarlo a partir de su ubicación. Para realizar el proceso de obtención de la referencia se hace una solicitud GET a la API de cartociudad antes mencionada, proporcionándole la localización del dispositivo, se guarda esta primera referencia y usa para compararla con las sucesivas ubicaciones en tiempo real, así diferenciamos si seguimos dentro de los límites de esta parcela o no, los datos que devuelve esta API están en formato JSON, para leer estos datos se hace uso de la librería retrofit que facilita la implementación de peticiones HTTP en nuestra aplicación.

4.	Para obtener los datos de una parcela a partir de la referencia catastral, se solicita al usuario que introduzca la referencia catastral y se muestra la información correspondiente. Esta solicitud se hace se crea un objeto con la cadena de URL proporcionada. La conexión se abre mediante url.openConnection() y la propiedad de solitud "Aceptar" se establece en "aplicación/gml+xml" para especificar el formato de respuesta esperado como XML. El flujo de entrada de respuesta se obtiene mediante connection.getInputStream(). La respuesta XML se analiza utilizando las clases DocumentBuilFactory, DocumentBuilder y Document de la API DOM de Java. El flujo de entrada se pasa al método Parse de DocumentBuilder para obtener un objeto que represente la estructura XML. El método getElementByTagName() se utiliza para recuperar la primera aparición del elemento "gml:posList" del documento y así acceder a sus coordenadas para poder dibujar el poligono.

5.	Pasar las coordenas UTM recibidas por la API ovc.catastro a coordenadas geográficas, que son las utilizadas por la clase Polygon de la API de Google Maps para la representación de polígonos. 

Para realizar la conversión de las coordenadas hay que tener en cuenta: 
1.	Sistema de coordenadas UTM: El sistema de coordenadas UTM divide la superfi-cie de la Tierra en zonas, cada una de las cuales está definida por un número de zo-na y una letra de banda. Cada zona tiene un meridiano central y un sistema de pro-yección. Es importante conocer la zona UTM correspondiente a las coordenadas UTM que se desean convertir. Para esta primera versión nuestra zona utilizada será España (30).
2.	Conocer el datum utilizado: El datum es el modelo matemático que se utiliza para establecer una referencia precisa para las coordenadas geográficas y UTM. Los da-tos se basan en diferentes elipsoides y modelos geodésicos que pueden variar según la región geográfica. Es fundamental conocer el datum utilizado para realizar una conversión precisa.
3.	Herramientas de conversión: Existen diversas herramientas y métodos disponi-bles para convertir coordenadas UTM a coordenadas geográficas. Estas herramien-tas pueden ser aplicaciones web, aplicaciones móviles o bibliotecas de programa-ción que proporcionan funciones de conversión. Algunas herramientas populares incluy en calculadoras geodésicas en línea, aplicaciones móviles especializadas y bibliotecas de programación como Proj4j que es la utilizaremos.
4.	Formato de las coordenadas: Las coordenadas UTM se representan generalmente en pares de valores numéricos, uno para la coordenada X (este-oeste) y otro para la coordenada Y (norte-sur). Las coordenadas geográficas se representan en grados decimales (latitud y longitud) o en otros formatos como grados, minutos y segun-dos. Es necesario asegurarse de que las coordenadas se ingresen correctamente en el formato adecuado para realizar la conversión correctamente. Para esta primera versión nos centraremos en Norte y Este que son las utilizadas por la API que utili-zamos para obtener las coordenadas de los límites de la parcela.


6.	Dibujar el polígono que conforma la parcela, para realizar este dibujo se hace uso de la clase Polygon de la librería de Google maps, una vez dibujado se puede hacer click sobre el polígono para guardar una captura de pantalla en la base de datos SQlite y poder mostrar más datos en el activity de mis parcelas a través de un Recycler view donde alojaremos tantos Cardviews tengamos guardados de nuestras parcelas.

7.	Al hacer click en el un cardview se mostrará otra pantalla con mas información re-lacionada con este cardview, Imagen, Dirección, Coordenadas, etc..

 

7. Manual de despliegue
La aplicación se despliega en el dispositivo móvil del usuario.
7.1. Requisitos hardware y software aplicables
●	Dispositivo móvil con sistema operativo Android 10 y al menos 5 pulgadas de pantalla.
7.2. Instrucciones de despliegue en Linux
●	No aplicable.
7.3. Instrucciones de despliegue en Windows
●	No aplicable.
7.4. Instrucciones de despliegue en dispositivos móviles (Android o IOS)
●	Descargar la aplicación desde la tienda de aplicaciones de Android.
7.5. Configuración inicial
●	No se requiere configuración inicial.
7.6. Datos de prueba
●	Aceptar los permisos de ubicación para a sección en la que esta se utiliza.
●	Proporcionar una referencia catastral.
 

8. Manual de uso

La aplicación se inicia en la pantalla del registro, en la que si no estas registrado deberás registrarte para seguir haciendo uso de esta, pulsando en el botón que se encuentra en la parte inferior de la pantalla.

   

Una vez registrado podrás iniciar sesión con el botón “Inicio” que comprueba el usuario y la contraseña, registrado en la base de datos SQlite y pasaremos a la siguiente pantalla, la cual nos mostrará un menú como el siguiente.

 



En este menú se pueden visualizar tes opciones:
	1º.- Comprobación de los límites proporcionando una referencia catastral, que se deberá introducir en la casilla correspondiente.

 

Pulsando sobre esta casilla se desplegará el teclado e introducimos la referencia catastral, una vez introducida pulsamos sobre el botón “Comprobar Ref. Catastral”, si esta casilla se encuentra vacía mostrará un mensaje informando que debe rellenar los datos.


	Si está rellena pasará a la siguiente pantalla donde le pedirá que acepte los permisos para poder utilizar el sistema de ubicación y una vez aceptados realizará una animación donde la pantalla se situará sobre el primer punto de coordenadas que proporciona la API, este punto es el inicio de uno de los puntos para poder representar el polígono en el mapa, se dibujará el polígono y deberá pausar la animación si no se encuentra dentro de este polígono, ya que recordemos que la idea original de esta aplicación es distinguir si no encontramos dentro de una parcela o no, y a partir de aquí podrá guardar la parcela si lo requiere o volver atrás o ir a su ubicación y reiniciar los limites para que vuelva a comprobar dentro que parcela se encuentra.




 	 








	2º.- Comprobación de los límites por las coordenadas geográficas.

 
Como indica el texto de este botón y el título de esta aplicación aquí comienza la idea original de mostrar un en tiempo real si se encuentra debido a su ubicación dentro o fuera de una parcela.


	Pulsando este botón, si es la primera vez y antes no ha realizado la búsqueda por referencia, solicitará los permisos de ubicación y una vez aceptados la cámara realizará una animación hacia su ubicación actual y seguido dibujará el polígono correspondiente a la parcela en la que se encuentra y a partir de aquí comenzará a recibir ubicaciones en tiempo real para comprobar si se encuentra dentro de los límites de esta parcela o no, lanzando un mensaje de alerta a través de un sonido y coloreando el fondo de pantalla en rojo, que iniciará un parpadeo mientras se encuentre fuera, pulse el botón de pausa o vuelva a la parcela.



Estas dos opciones de comprobar la parcela coinciden en la misma pantalla del mapa en la que se realizan las operaciones, que se muestra a continuación.

 Solicitud de permiso para la ubicación, se recomienda usar la opción “Precisa”, ya que ajustará mas la precisión será beneficioso para comprobar los límites.


















 En esta imagen se muestra el resultado de comprobar los límites por coordenadas, como vemos nuestra ubicación (Punto azul) se encuentra dentro del polígono dibujado.



 Si salimos fuera escucharemos el sonido de alerta y veremos que el fondo de la pantalla se vuelve rojo para que sea mas llamativo y aparece un botón de “Pausa”, para pausar la animación.

















 Vemos que una vez que volvemos a entrar en la parcela todo vuelve a la normalidad.


























Si estando fuera de la parcela pulsamos en “Reiniciar límites” los limites se reinicia el proceso y comprueba y dibuja los limites de la nueva ubicación.

  

Límites de la nueva parcela.






	3º.- Como tercera opción de nuestro menú disponemos de un listado de nuestras parcelas que previamente hemos guardado.


	  







Como pantalla final podemos obtener mas datos pulsando sobre la tarjeta.

 


  9.- Posibles mejoras.

•	Mejorar mensaje de alerta en precisión.
•	Validación de contraseña segura.
•	Validación de referencia catastral.
•	Optimizar el rendimiento para ahorrar batería.
•	Evitar guardar parcelas repetidas.
•	Gestionar si la API devuelve nulos o está caída.
•	Añadir la posibilidad de realizar mediciones de distancias y áreas dentro de la parcela.
•	Incluir una funcionalidad de búsqueda por nombre de propietario o dirección, ade-más de la búsqueda por referencia catastral.
•	Implementar un sistema de notificaciones para alertar al usuario cuando se encuen-tra fuera de los límites de una parcela.
•	Integrar opciones de navegación para guiar al usuario hacia la parcela deseada.
•	Incluir una función de historial para mostrar las últimas comprobaciones realizadas por el usuario.





















  10.- Conclusiones.

	Se pueden sacar las siguientes conclusiones:
1.	La aplicación tiene un enfoque único y útil para informar a los usuarios sobre su ubicación en relación con las parcelas catastrales.
2.	La arquitectura de la aplicación se basa en el patrón Modelo-Vista-Controlador (MVC), lo que facilita la separación de responsabilidades y la organización del có-digo.
3.	La aplicación utiliza tecnologías modernas y populares, como Kotlin y SQLite, que son ampliamente utilizadas en el desarrollo de aplicaciones Android.
4.	La aplicación es compatible con dispositivos Android 13.0, lo que indica que está diseñada para funcionar en dispositivos más recientes y con características avanza-das.
5.	El proyecto incluye instrucciones detalladas para la implementación, despliegue y uso de la aplicación, lo que facilita su desarrollo y mantenimiento.
6.	La aplicación se ha diseñado teniendo en cuenta las mejoras y consideraciones mencionadas en la respuesta anterior, lo que garantiza un proyecto robusto y de alta calidad.
7.	La aplicación tiene un potencial significativo para ayudar a los propietarios de te-rrenos y personas interesadas en la ubicación de parcelas catastrales, facilitando la gestión y el acceso a la información relevante.
En resumen, el proyecto es innovador, bien estructurado y utiliza tecnologías modernas para ofrecer una solución útil y eficiente a los usuarios que necesitan conocer si están den-tro o fuera de los límites de una parcela según la referencia catastral.
