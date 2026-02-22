package pe.edu.cibertec.recetario.domain.model

/**
 * Modelo de Dominio de una Receta.
 * Es el objeto principal que utiliza la interfaz de usuario para mostrar información.
 * A diferencia del DTO, este objeto es limpio y contiene solo lo necesario para la lógica de la app.
 */
data class Recipe(
    val id: Long,               // Identificador único de la receta
    val userId: Long,           // ID del usuario que la creó
    val title: String,          // Título del plato
    val description: String,    // Procedimiento paso a paso
    val visibility: String,     // "public" o "private"
    val createdAt: Long,        // Marca de tiempo de creación
    val imageUrl: String?,      // URL pública de la imagen (ajustada por el Mapper)
    val videoUrl: String?,      // URL pública del video (ajustada por el Mapper)
    val authorEmail: String?,   // Correo del autor para mostrar en pantalla
    val isMine: Boolean         // Indica si la receta fue creada por el usuario actual (logueado)
)
