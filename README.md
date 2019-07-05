# Cliente Traza de Evacuación

Aplicación de android que permite obtener las localizaciones gps y el estado de batería del usuario durante un simulacro. La aplicación se encuentra en el repositorio [Google Play Store][app-store] para su uso público. El backend de este cliente se encuentre [en este repositorio][backend]

## Modo de funcionamiento
Al momento de ejecutar la aplicación, esta empieza a recolectar información como geolocalización y nivel de batería. La información es almacenada en la persistencia del dispositivo a través de *SharedPreferences*. Luego, toda la información se envía al Backend.

El funcionamiento temporal de la app y el backend es el siguiente:

[![N|Solid](https://i.imgur.com/HY0RIgp.png)](https://imgur.com/a/kGXUyt3)

1 El usuario descarga la App.
2 El usuario inicia la App. Se ejecutan:
 - Un **Servicio en Primer Plano** (***foreground service***) en un Thread aparte, el cual recibe localizaciones cada 15 segundos y la almacena* en persistencia .
 - **operacionesSegundoPlano** : Un Thread aparte (un solo hilo) que cada 60 segundos realiza las operaciones:
   -- enviarLocalizaciones: Se hace Prueba de Ping al backend -> Si hay Internet: enviar basch de localizaciones al backend, el cual las procesa y almacena en MongoDB.
   -- advertirLocalizacionesPendientes: Si el Ping no fue exitoso (no hay internet) y aún existen localizaciones en persistencia, entonces se muestra un mensaje de advertencia en la aplicación para que el usuario se conecte a internet.
   -- estadoDeSimulacro: el backend indica si el simulacro terminó. La aplicación termina de recopilar localizaciones.
   -- estadoDeLaAplicacion: Si el simulacro terminó y ya se enviaron todas las localizaciones almacenadas, se hace una notificación al usuario del fin del simulacro y se cierra automaticamente la App.

El tamaño máximo de almacenamiento para la App es de 10MB (~50.000 localizaciones, ~8 días). El uso de batería de esta es mínimo, ya que no obliga a mantener encendido el GPS y el envío de localizaciones al servidor es cada 60 segundos.

Durante toda la ejecución de la aplicación se mantiene una notificación persistente sobre la ejecución de la App. Este significa que se mantiene corriendo el foreground service.

## Arquitectura de la Aplicación

 La aplicación sigue una arquitectura MVC. 
 - **MainActivity**: La unica actividad, sólo carga la vista de la App, el servicio en primer plano para la toma de localizaciones, y el Thread que invoca operaciones lógicas en segundo plano.
 - **ModeloLogico**: es el controlador, contiene los métodos que ejecutan la lógica de la aplicación.
 - **Comunicador**: método asincrono (extiende asyncTask) genérico que realiza llamados GET y POST al Backend. Recibe como entrada un objeto del tipo HttpInputAsyncParams (request) y devuelve un objeto HttpOutputAsyncParams (response). No tiene problemas en esperar sincronamente la respuesta, ya que se ejecuta en un hilo aparte del Main. Siempre debe usarse esta clase o una similar para operaciones "pesadas", y nunca de una Actividad ya que se puede producir un ARN.
 - **LocationUpdateService**: Servicio que es creado y al que se enlaza el hilo principal (MainActivity). Recibe callbacks de las localizaciones del dispositivo y las almacena en persistencia.

## Consideraciones Finales

 - En el manifest se utiliza 
` <activity android:name=".MainActivity" :launchMode="singleTask">`
Por lo tanto si la App esta siendo ejecutada y se va al Home, al volver a la aplicación se retomará la App desde el punto en el que estaba y no se creará una nueva tarea. Esto debido a que al "volver" a la App desde la Notificación, se creaba una nueva instancia de la actividad.
 - La tecla "Volver" al ser presionada desde la aplicación provocaba el cierre de la App. Por lo tanto, se hizo Override de esta y se le implemento una función para volver al Home, impidiendo que el usuario cierre la App. TODO: hacer un botón explícito para que el usuario cierre la App.



   [app-store]: <https://play.google.com/store/apps/details?id=com.citiaps.locationupdate&hl=es_CL>
   [backend]: <https://github.com/citiaps/backend-traza-evacuacion>
