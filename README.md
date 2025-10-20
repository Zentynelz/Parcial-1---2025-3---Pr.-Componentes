# F1 Pit Stop App - Módulo de Datos (Persona 1)

Aplicación Android desarrollada en Kotlin para registrar, visualizar y analizar pit stops de Fórmula 1.  
Combina una arquitectura **MVVM (Model–View–ViewModel)** con **Jetpack Compose** para la interfaz de usuario, **Room** para la persistencia local de datos y **Y-Charts** para la visualización de estadísticas gráficas.

## Descripción del Proyecto

El proyecto permite:
- Registrar pit stops con validación completa de los datos.
- Consultar y filtrar registros de pit stops almacenados localmente.
- Obtener estadísticas de rendimiento (tiempo promedio, pit stop más rápido, total de registros, etc.).
- Visualizar los resultados mediante gráficos interactivos con Compose.

Está diseñado con un enfoque modular y escalable, priorizando la mantenibilidad, el testing y la integración entre capas.


## Arquitectura Implementada

### Patrón MVVM + Repository
- **Model**: Entidades de datos (PitStop, enums)
- **ViewModel:** Gestiona la lógica de presentación y comunica la UI con el Repository.
- **Repository**: Abstrae el acceso a datos y maneja la lógica de negocio
- **Database**: Room Database para persistencia local

### Estructura de Paquetes
```
com.f1pitstop.app/
├── data/
│ ├── database/ # Room Database, DAO, Converters
│ ├── model/ # Entidades y modelos de datos
│ ├── repository/ # Repository Pattern y lógica de negocio
│ └── exception/ # Excepciones personalizadas
├── ui/
│ ├── summary/ # Pantalla de resumen con Compose y gráficos
│ ├── components/ # Componentes reutilizables de la UI
│ └── theme/ # Temas y estilos visuales
├── utils/ # Utilidades y factories de validación/datos
├── PitStopApplication.kt # Inicialización global del Repository y DB
└── MainActivity.kt # Actividad principal y navegación
```

## Funcionalidades Implementadas

### ✅ Guardar Pit Stop (1 punto)
- Validación completa de datos de entrada
- Persistencia en base de datos Room
- Manejo de errores y excepciones
- Soporte para todos los campos requeridos

### ✅ Pruebas Unitarias (0.5 puntos)
- Tests unitarios para DAO (15+ tests)
- Tests unitarios para Repository (20+ tests)
- Tests de integración (8+ tests)
- Cobertura completa de validaciones y casos edge


#### Justificación Técnica

### Arquitectura MVVM
- Separación de responsabilidades: desacopla la lógica de negocio de la UI.  
- Reactividad total: mediante `Flow`, la UI se actualiza automáticamente ante cambios en los datos.  
- Testeabilidad: permite realizar pruebas unitarias del ViewModel y Repository sin depender de la UI.  
- Ciclo de vida gestionado: los ViewModels sobreviven a cambios de configuración, evitando pérdida de datos.


### Persistencia con Room Database
- Abstracción de SQLite: facilita el trabajo con entidades, DAOs y migraciones.
- Seguridad en tiempo de compilación: verifica las consultas SQL.
- Compatibilidad con Coroutines y Flow: soporte asíncrono nativo.
- Manejo de migraciones: mantiene la integridad del esquema de la base de datos. 

---

### Interfaz de Usuario con Jetpack Compose
- Declarativa: simplifica el desarrollo al describir el estado visual sin XML.  
- Menos código, más legibilidad: las funciones composables son reutilizables.  
- Reactividad nativa: integración directa con `StateFlow` y `LiveData`.  
- Alta productividad: vista previa en tiempo real desde Android Studio.

---

### Visualización de Datos con Y-Charts
- Librería optimizada para Jetpack Compose, usada para mostrar los tiempos de pit stops.  
- Ofrece gráficos de barras personalizados con colores, animaciones y escalas dinámicas.  
- Ideal para representar los resultados recientes o las estadísticas generales del piloto o escudería.

Referencia:  
[Y-Charts for Jetpack Compose](https://github.com/codeandtheory/YCharts)

---

### Kotlin Coroutines y Flow
- Asincronía simplificada: operaciones sin bloqueo del hilo principal.  
- Gestión eficiente: uso de `suspend functions` para inserciones, lecturas y actualizaciones.  
- Reactividad: `Flow<List<PitStop>>` emite cambios en tiempo real hacia la UI.

---


## Componentes Principales

### 1. Modelo de Datos

#### PitStop Entity
```kotlin
@Entity(tableName = "pit_stops")
data class PitStop(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val piloto: String,
    val escuderia: Escuderia,
    val tiempoTotal: Double,
    val cambioNeumaticos: TipoNeumatico,
    val numeroNeumaticosCambiados: Int,
    val estado: EstadoPitStop,
    val motivoFallo: String? = null,
    val mecanicoPrincipal: String,
    val fechaHora: Long = System.currentTimeMillis()
)
```

#### Enums Definidos
- **Escuderia**: Mercedes, Red Bull, Ferrari, McLaren, Alpine, Aston Martin, Williams, Alfa Romeo, Haas, AlphaTauri
- **TipoNeumatico**: Soft, Medium, Hard
- **EstadoPitStop**: OK, Fallido

### 2. Base de Datos Room

#### PitStopDao
Operaciones CRUD completas:
- `getAllPitStops()`: Obtiene todos los pit stops ordenados por fecha
- `insertPitStop()`: Inserta nuevo pit stop
- `updatePitStop()`: Actualiza pit stop existente
- `deletePitStop()`: Elimina pit stop
- `searchPitStops()`: Búsqueda por piloto o escudería
- `getFastestTime()`: Tiempo más rápido
- `getAverageTime()`: Promedio de tiempos
- `getTotalCount()`: Total de pit stops

### 3. Repository Pattern

#### PitStopRepository
- Abstrae el acceso a datos
- Implementa validaciones de negocio
- Maneja excepciones y errores
- Proporciona métodos para estadísticas

### 4. Validaciones Implementadas

#### Validaciones de Datos
- **Piloto**: No vacío, 2-50 caracteres, solo letras y espacios
- **Tiempo**: Mayor a 0, máximo 300 segundos
- **Neumáticos**: Entre 1 y 4 neumáticos
- **Mecánico**: No vacío, 2-50 caracteres
- **Motivo Fallo**: Requerido cuando estado es "Fallido"
- **Fecha**: No puede ser futura

### 5. Utilidades

#### PitStopFactory
- Genera datos de prueba realistas
- Crea pit stops específicos para testing
- Proporciona datos de ejemplo para desarrollo

#### ValidationUtils
- Utilidades de validación reutilizables
- Validación de rangos, longitudes, patrones
- Combinación de múltiples validaciones

## Testing

### Cobertura de Pruebas
- **PitStopDaoTest**: 15 tests para operaciones de base de datos
- **PitStopRepositoryTest**: 20+ tests para lógica de negocio
- **PitStopIntegrationTest**: 8 tests de integración completa

### Tipos de Tests
1. **Unitarios**: Componentes aislados con mocks
2. **Integración**: Flujo completo Repository → DAO → Database
3. **Validación**: Todos los casos de validación de datos

## Configuración del Proyecto

### Dependencias Principales
```gradle
// Room Database
implementation "androidx.room:room-runtime:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"

// Testing
testImplementation 'junit:junit:4.13.2'
testImplementation 'org.mockito.kotlin:mockito-kotlin:5.2.1'
testImplementation 'androidx.room:room-testing:2.6.1'
testImplementation 'com.google.truth:truth:1.1.4'
```

### Configuración de Room
- Base de datos en memoria para testing
- TypeConverters para enums personalizados
- Singleton pattern para instancia de base de datos

## Integración con Otros Módulos

### Interfaces Públicas
El módulo expone las siguientes interfaces para integración:

```kotlin
// Obtener instancia del repository
val repository = (application as PitStopApplication).repository

// Operaciones principales
repository.insertPitStop(pitStop)
repository.getAllPitStops() // Flow<List<PitStop>>
repository.searchPitStops(query) // Flow<List<PitStop>>
repository.getStatistics() // PitStopStatistics
```

### Datos para UI
- **Flow<List<PitStop>>**: Para listas reactivas
- **PitStopStatistics**: Para pantalla de resumen
- **ValidationResult**: Para mostrar errores de validación

## Justificación Técnica

### Decisiones de Arquitectura

1. **Room Database**: 
   - Abstracción sobre SQLite
   - Type safety en compile time
   - Soporte nativo para Coroutines y Flow

2. **Repository Pattern**:
   - Abstrae la fuente de datos
   - Centraliza la lógica de negocio
   - Facilita testing con mocks

3. **Flow para Observabilidad**:
   - Actualizaciones reactivas en UI
   - Manejo automático de cambios en datos
   - Integración natural con Compose

4. **Validaciones Centralizadas**:
   - Consistencia en toda la aplicación
   - Reutilización de lógica de validación
   - Separación de responsabilidades

### Beneficios de la Implementación

- **Mantenibilidad**: Código bien estructurado y documentado
- **Testabilidad**: Alta cobertura de tests unitarios e integración
- **Escalabilidad**: Arquitectura preparada para nuevas funcionalidades
- **Robustez**: Manejo completo de errores y validaciones

## Próximos Pasos

Para completar la aplicación, los otros módulos deben:

1. **Persona 2 (UI)**: Implementar pantallas usando el repository
2. **Persona 3 (Resumen)**: Usar `getStatistics()` para mostrar datos
3. **Integración**: Conectar ViewModels con el repository

## Comandos de Testing

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar solo tests unitarios
./gradlew testDebugUnitTest

# Generar reporte de cobertura
./gradlew jacocoTestReport
```

---

**Desarrollado por**: 
Persona 1 - Backend y Persistencia  
Persona 3 – interfaz y Datos estadisticos
**Puntos obtenidos**: 1.5 (Guardar pit stop: 1.0 + Pruebas unitarias: 0.5)  
**Fecha**: Octubre 2025