# F1 Pit Stop Tracker

Aplicación Android completa desarrollada en Kotlin para registrar, visualizar y analizar pit stops de Fórmula 1.  
Combina una arquitectura **MVVM (Model–View–ViewModel)** con **Jetpack Compose** para la interfaz de usuario, **Room** para la persistencia local de datos, validación robusta y un tema oscuro elegante inspirado en F1.

## Descripción del Proyecto

## Funcionalidades Principales

- **Registrar Pit Stops**: Formulario completo con validación robusta y campos condicionales
- **Lista de Pit Stops**: Visualización con búsqueda y opciones de editar/eliminar
- **Estadísticas**: Pantalla de resumen con métricas de rendimiento
- **Navegación Completa**: Interfaz fluida con botones de regreso
- **Tema Oscuro F1**: Diseño elegante sin elementos blancos, inspirado en Fórmula 1
- **Validación Inteligente**: Previene crashes y guía al usuario con mensajes claros
- **Persistencia Local**: Datos guardados localmente con Room Database

La aplicación está diseñada con enfoque en la experiencia de usuario, robustez y mantenibilidad del código.


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
│ ├── summary/ # Pantalla de resumen con estadísticas
│ ├── edit/ # Pantalla de crear/editar pit stops
│ ├── list/ # Pantalla de lista con búsqueda
│ └── theme/ # Tema oscuro F1 y estilos
├── utils/ # Utilidades y factories de datos
├── PitStopApplication.kt # Inicialización global
└── MainActivity.kt # Actividad principal
```

## Funcionalidades Implementadas

### Aplicación Completa
- **Pantalla de Resumen**: Estadísticas y navegación principal
- **Crear/Editar Pit Stops**: Formulario con validación completa
- **Lista de Pit Stops**: Búsqueda, edición y eliminación
- **Navegación Fluida**: Botones de regreso en todas las pantallas
- **Tema Oscuro**: Diseño elegante sin elementos blancos
- **Validación Robusta**: Previene crashes por datos inválidos
- **Campo Condicional**: Motivo de fallo solo aparece cuando es necesario

### Pruebas Unitarias Completas
- Tests unitarios para DAO (15+ tests)
- Tests unitarios para Repository (20+ tests)  
- Tests de integración (8+ tests)
- Cobertura completa de validaciones y casos edge
- Tests funcionando con Robolectric


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

### Tema Oscuro F1
- Colores inspirados en Fórmula 1: rojo Ferrari, azul racing, naranja McLaren
- Eliminación completa de elementos blancos para mejor experiencia visual
- Gradientes suaves y cards con bordes redondeados
- Contraste optimizado para legibilidad en modo oscuro
- Diseño minimalista sin emojis, enfocado en profesionalismo

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
// Jetpack Compose
implementation platform('androidx.compose:compose-bom:2024.10.01')
implementation 'androidx.compose.ui:ui'
implementation 'androidx.compose.material3:material3'
implementation 'androidx.activity:activity-compose:1.9.3'

// Room Database
implementation "androidx.room:room-runtime:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"
ksp "androidx.room:room-compiler:2.6.1"

// Navigation
implementation 'androidx.navigation:navigation-compose:2.8.3'

// Testing
testImplementation 'junit:junit:4.13.2'
testImplementation 'org.mockito.kotlin:mockito-kotlin:5.3.1'
testImplementation 'androidx.room:room-testing:2.6.1'
testImplementation 'com.google.truth:truth:1.4.4'
testImplementation 'org.robolectric:robolectric:4.13'
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

## Instalación y Uso

### Generar APK
```bash
# Generar APK de debug (recomendado)
gradlew assembleDebug

# El APK se genera en:
# app/build/outputs/apk/debug/app-debug.apk
```

### Instalación en Dispositivo
1. Habilitar "Fuentes desconocidas" en Configuración > Seguridad
2. Transferir el APK al dispositivo
3. Tocar el archivo APK para instalar

### Uso de la Aplicación
1. **Pantalla Principal**: Ver estadísticas y acceder a funciones
2. **Nuevo Pit Stop**: Llenar formulario con validación automática
3. **Lista**: Buscar, editar o eliminar pit stops existentes
4. **Navegación**: Usar botones de regreso para moverse entre pantallas

## Comandos de Testing

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar solo tests unitarios
./gradlew testDebugUnitTest

# Generar reporte de cobertura
./gradlew jacocoTestReport
```

## Características Técnicas Destacadas

### Validación Robusta
- Previene crashes por campos vacíos o datos inválidos
- Mensajes de error claros y específicos
- Validación en tiempo real con feedback visual

### Campo Condicional Inteligente
- Campo "Motivo de Fallo" solo aparece cuando estado = "Fallido"
- Se oculta automáticamente cuando estado = "OK"
- Validación contextual según el estado seleccionado

### Experiencia de Usuario
- Tema completamente oscuro sin elementos blancos molestos
- Navegación intuitiva con botones de regreso
- Diseño responsive con scroll automático
- Indicadores de carga durante operaciones

### Arquitectura Sólida
- Patrón MVVM con Repository
- Separación clara de responsabilidades
- Manejo centralizado de errores
- Testing completo con alta cobertura

---

**Aplicación F1 Pit Stop Tracker**  
**Desarrollado en**: Kotlin + Jetpack Compose  
**Base de Datos**: Room Database  
**Arquitectura**: MVVM + Repository Pattern  
**Testing**: JUnit + Mockito + Robolectric  
**Fecha**: Octubre 2025