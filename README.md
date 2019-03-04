### Java Test

Este repositorio contiene mi solución al ejercicio técnico "Code Review / Refactoring exercise".

El archivo Original.java contiene el código original enviado. El archivo JobLogger.java contiene el código ya refactorizado. El archivo Tests.java contiene las pruebas unitarias para probar el código refactorizado.

##### Code Review and feedback

1. La primera observación que le hago al código es la cantidad de variables booleanas usadas para parametrizar la clase y definir su comportamiento. Esto vuelve el código más difícil de leer y mantener en el tiempo, ya que ante cualquier cambio que sea necesario realizar en la lógica del logger necesariamente hay que recorrer cuidadosamente cada línea del código para evitar dejar algo sin hacer, debido a que su uso está distribuido muy desorganizadamente tanto en el constructor como en el método LogMessage.

2. En segundo lugar, en el método LogMessage siempre están siendo inicializado todos los objetos necesarios para realizar los distintos tipos de logs a pesar de que no necesariamente puedan ser utilizados. Siempre se inicia una conexión con la Base de datos, se abre un archivo e incluso se crea de ser necesario y se inicializa el manejador de la consola, vayan a ser utilizados o no. Esto debe ser necesariamente modificado.

3. Generalizando el punto anterior, es necesario reorganizar y distribuir el código de manera que todas esas tareas no estén escritas en el método de LogMessage, sino colocarlas en métodos aparte los cuales sean llamados únicamente cuando sea necesario, y de hacerlo realizar las inicializaciones o conexiones pertinentes. Estos métodos pueden pertenecer a la misma clase, o haciendo uso de las técnicas de diseño orientado a objetos en clases distintas. Haciendo esto se contribuiria enormemente con la legibilidad y mantenibilidad del código.

4. La variable "l" (no muy buen nombre, por cierto) es inicializada en null y luego se le agrega un valor concatenando un string generado a su valor actual, lo cual causa que todo log tenga de prefijo "null". La variable "t" no tiene un nombre muy indicativo de su función, y no queda clara a simple vista.

5. Para el caso de hacer logs a archivos y consola, se está haciendo log del mensaje puro que llega al método, y no se utiliza el string generado de acuerdo al tipo de log y la fecha. En el caso de logs a la base de datos el mensaje a ser guardardado es únicamente el valor de la variable booleana 'message' la cual llega como parámetro de la llamada al método.

6. En el código dado se establecen conexiones con una base de datos y se abren archivos de texto, pero en ningún lugar del código se aprecia como estas conexiones sean finalizadas o dichos archivos cerrados. Seguramente al eliminarse el Scope donde viven dichos objetos las mencionadas conexiones sean cerradas, pero sería una mejor práctica manejarlo manualmente. Más aún, dependiendo de la frecuencia con la que el logger escriba en los distintos medios podría ser más conveniente mantener las conexiones abiertas en lugar de conectar y desconectar cada vez que se va a realizar una inserción/escritura.


#### Code refactor

Decidí trasladar las diferentes lógicas de hacer log a clases que se encarguen cada una de gestionarlas a su manera. La clase abstracta "MyLogger" ofrece el elemento común de cada manera distinta de hacer log, y cada especificación de esa clase hace log a su manera, logrando que mediante el patrón estrategia para la clase JobLogger sea totalmente transparente cómo es efectuado el log ya que esta maneja una linked list de elementos de la clase "MyLogger" y les pide a todos que hagan log.

De esta manera se aumenta la mantenibilidad y legibilidad del código, además de que si se desea agregar nuevas maneras de hacer log (por ejemplo, a un recurso remoto) basta con implementar una nueva clase que extienda la clase "MyLogger" y encargarse de que una instancia de dicha clase recién creada llegue al atributo "jobLoggers" de la clase.

El cambio realizado permite un método "LogMessage" capaz de cumplir su tarea sin importar cuantos objetos "MyLogger" existen en la lista "jobLoggers", lo cual asoma la posibilidad de volver la clase dinámicamente configurable agregando métodos capaces de insertar y eliminar objetos "MyLoggers" de dicha lista. Actualmente el constructor de JobLogger mantiene su firma y configura la clase de acuerdo a los mismos parámetros booleanos recibidos para decidir si va a permitir hacer log a base de datos, archivo de texto o consola, pero si se desea podría ser modificada para eliminar dichos parámetros, configurarla agregando objetos a "jobLoggers" y así haber separado completamente las distintas lógicas de hacer log de la clase JobLogger.

Con el refactor realizado se logró eliminar código repetido o redundante, evitar conexiones innecesarias y organizar mejor partes importantes del código para que se les pueda dar un mejor mantenimiento. El método LogMessage se redujo de 69 líneas a 25 líneas.