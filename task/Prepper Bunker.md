# Assignment Building a Prepper Bunker Management System

## Objective

Your task is to develop a full-stack application to manage resources in a Prepper Bunker. The system will track items such as weapons, ammunition, food, drinks, medications, fuel, batteries, and generators. The backend will provide a RESTful API using Quarkus, while the frontend will offer a modern UI built with Angular Renaissance and Angular Material. This assignment is divided into two parts backend and frontend development.

---

## Part 1 Backend Development with Quarkus

### Instructions

You will create a RESTful API to manage the bunker’s resources, supporting CRUD operations (Create, Read, Update, Delete) and additional functionalities. The database schema is provided in an `import.sql` file, and your entities must align with it.

#### 1. Project Setup

- Template Use the provided Quarkus project template, which includes
  - `quarkus-hibernate-orm-panache` - Simplified ORM and repository management.
  - `quarkus-rest-jackson` - JSON serializationdeserialization.
  - `quarkus-jdbc-h2` - H2 in-memory database.
  - `quarkus-smallrye-openapi` - API documentation.
- Database The schema is preloaded via `import.sql`. Ensure entities match the defined tables.

#### 2. Entities

Create JPA entities for each resource, such as
- `StorageLocation` - Physical location in the bunker.
- `Weapon` - Weapon details (e.g., type, model).
- `AmmunitionStock` - Ammunition quantities by type.
- `AmmunitionType` - Ammunition type details (e.g., caliber, type).
- `Food` - Food items with expiration dates.
- `Drink` - Drinkable items.
- `Medication` - Medical supplies with expiration dates.
- `Fuel` - Fuel by type and quantity.
- `FuelType` - Fuel type details (e.g., name).
- `Battery` - Battery stocks.
- `Generator` - Generator details.

![ERD](./Prepper%20Bunker.png)
Full ERD: [plantuml online](https://plantuml.online/uml/rLNFIyCm5BxthtZU5F4Yx396eRDr3cNAh8AdaINF6MoRcAJC8Vxljd6wsiLrm-xWAV3olVTzxqTU3imJfWZJ94UfU6Qy9yuGA5SqGS-Z2T4Qj16Ap72I24eC5ncyUG37m1bqeJyS34Snz7etbxvV78De58s3E0gX_m1XT1xSNSKGtC8mZ4RnC7xmVUkRaOAq2_V1-EmY61-UTZf7rivGKyMbPQbTlt_98McQPxo4JCn2OWjgA4du59LBa6NuJSaSYMob72wMqfuAXedhuYKdcU5cKTmLVDPKAylafvxpooLLlPakdAlKXHzzRY7LBMKw4jn9EIk6-NcFdQde2w_l-bUHjUAhvEfJ72EcT0mBYjtLFmh1_gtu51dV-w9mpCS6_J9NKcXdeDtwacFIrj9nPVQ3g0MERxrRFBFA9EnKaoan1jLYZ-DAYIHqJUHVDyGCCop6HQWzAfTYRh-LYkp6bcX3JAx_CAhBN-dan5_xAhkGQzHLnDhVthKcP42CjxR-tJ5vkR2K6xqn_kUIReJPRTY8ggoTveZbyofHgmTTfh6oR_J-clX6u6h4wbmgppI8h8RO5Em3) ([mermaid version](https://www.mermaidchart.com/raw/3409ae54-f04a-43e0-9f92-3136b6395b1e?theme=light&version=v0.1&format=svg))

Use Panache ORM (`PanacheEntity` for automatic IDs or `PanacheEntityBase` for custom IDs) and map fields to database columns.

#### 3. Repositories

Implement a `PanacheRepository` for each entity, providing
- Basic CRUD Create, read, update, delete.
- Custom Queries
  - `findWeaponsByLocation(Long locationId)` - Weapons in a specific location.
  - `findExpiredItems()` - Expired food or medications.
  - `getTotalAmmunitionByType(Long typeId)` - Total quantity of an ammunition type across all stocks.

Use Panache query methods (e.g., `find()`, `list()`, or JPQL).

#### 4. Data Transfer Objects (DTOs)

Use DTOs to shape data for the frontend, e.g.
- `WeaponDTO` - `type`, `model`, `quantity`.
- `MedicationSummaryDTO` - `name`, `quantity`, `expirationDate`.
- `AmmunitionSummaryDTO` - `type`, `totalQuantity`.

DTOs are plain Java objects, mapped from entities in the service layer.

#### 5. REST Resources

Create resource classes (e.g., `WeaponResource`) with endpoints
- GET `resources` - List all items (e.g., `weapons`).
- GET `resources/{id}` - Get item by ID (e.g., `weapons/1`).
- POST `resources` - Create item (JSON body).
- PUT `resources/{id}` - Update item (JSON body).
- DELETE `resources/{id}` - Delete item.

Add specialized endpoints
- GET `weapons/by-location/{locationId}` - Weapons by location.
- GET `medications/expired` - Expired medications.
- GET `ammunition/total/{typeId}` - Total ammunition by type.

Use HTTP status codes (e.g., 200, 404, 400).

#### 6. Validation and Error Handling

- Validate data (e.g., `quantity > 0`, valid expiration dates).
- Return errors with status codes and messages (e.g., `{ error Quantity must be positive }`).

#### 7. API Documentation (optional)

Use `quarkus-smallrye-openapi` with annotations (`@OpenAPIDefinition`, `@Operation`, `@APIResponse`) to document endpoints (Purpose of each endpoint.). Access via `q/openapi`.

### Evaluation Criteria

- Correct entities and repositories with CRUD and custom queries.
- Proper DTO usage.
- Complete REST API with specialized endpoints.
- Robust validation and error handling.
- Comprehensive OpenAPI documentation.

---

## Part 2 Frontend Development with Angular Renaissance

### Project Setup and Bootstrap Information

The frontend is bootstrapped using Angular Renaissance with the command
```
ng new prepper-bunker-frontend --ssr=false --style=scss && cd prepper-bunker-frontend
```

It includes
- SCSS setup for advanced styling.
- HTTP Client using `provideHttpClient` for API calls.
- Angular Material Installed with `@angular/material` (@see https://material.angular.io/guide/getting-started) and a prebuilt theme (purple-green).

```
? Choose a prebuilt theme name, or "custom" for a custom theme: Purple/Green       [ Preview: https://material.angular.io?theme=purple-green ]
? Set up global Angular Material typography styles? Yes
? Include the Angular animations module? Include and enable animations
```

### Model Information

Define these TypeScript interfaces in `src/app/models` (e.g., `models/weapon.ts`) to match backend entities

```typescript
// models/storage-location.ts
export interface StorageLocation {
  id number;
  name string;
  description string;
}

// models/weapon.ts
export interface Weapon {
  id number;
  type string;
  model string;
  quantity number;
  ammunitionTypeId number;
  storageLocationId number;
}

export interface WeaponCreate {
  type string;
  model string;
  quantity number;
  ammunitionTypeId number;
  storageLocationId number;
}

export interface AmmunitionType {
  id number;
  caliber string;
  type string;
}

export interface AmmunitionStock {
  id number;
  ammunitionTypeId number;
  quantity number;
  storageLocationId number;
}

export interface AmmunitionStockCreate {
  ammunitionTypeId number;
  quantity number;
  storageLocationId number;
}

// models/drink.ts
export interface Drink {
  id number;
  type string;
  quantity number;
  expirationDate string;
  storageLocationId number;
}

export interface DrinkCreate {
  type string;
  quantity number;
  expirationDate string;
  storageLocationId number;
}

// models/food.ts
export interface Food {
  id number;
  type string;
  quantity number;
  expirationDate string;
  storageLocationId number;
}

export interface FoodCreate {
  type string;
  quantity number;
  expirationDate string;
  storageLocationId number;
}

// models/medication.ts
export interface Medication {
  id number;
  name string;
  quantity number;
  expirationDate string;
  purpose string;
  storageLocationId number;
}

export interface MedicationCreate {
  name string;
  quantity number;
  expirationDate string;
  purpose string;
  storageLocationId number;
}

// models/fuel.ts
export interface Fuel {
  id number;
  fuelTypeId number;
  quantity number;
  storageLocationId number;
}

export interface FuelType {
  id number;
  name string;
}

export interface FuelCreate {
  fuelTypeId number;
  quantity number;
  storageLocationId number;
}

// models/battery.ts
export interface Battery {
  id number;
  type string;
  capacity number;
  quantity number;
  storageLocationId number;
}

export interface BatteryCreate {
  type string;
  capacity number;
  quantity number;
  storageLocationId number;
}

// models/generator.ts
export interface Generator {
  id number;
  type string;
  power number;
  fuelTypeId number;
  status string;
  storageLocationId number;
}

export interface GeneratorCreate {
  type string;
  power number;
  fuelTypeId number;
  status string;
  storageLocationId number;
}
```

### Routes to Define

In `src/app/app.routes.ts`, define these routes
- `/weapons` - List of weapons.
- `/food` - List of food items.
- `/medications` - List of medications.
- `/storage-locations` - List of storage locations.
- `/ammunition` - List of ammunition stocks.
- `/fuel` - List of fuel stocks.
- `/batteries` - List of batteries.
- `/generators` - List of generators.
- `/**` - Not Found route for invalid URLs, linked to a `NotFoundComponent`.

Set the default route to `/weapons`.

### Components to Create

Use Angular Material components for a polished UI
- Navigation Bar `MatToolbar` with links to all routes.
- List Components `MatTable` for resource lists (e.g., weapons, food) with columns for key fields, and `MatButton` for actions (e.g., Delete, View Details).
- Detail Components `MatCard` for item details, `MatFormField` with `MatInput` for editable fields, and `MatButton` for Save and Cancel.
- Dialogs `MatDialog` for confirmations (e.g., delete prompts).
- Not Found Component Simple component with a Page Not Found message.
- Snackbar `MatSnackBar` for feedback (e.g., Item deleted).

### Services to Create

- Resource Services One for each resource (e.g., `WeaponService`, `FoodService`) to handle CRUD and specialized API calls (e.g., `getWeaponsByLocation`).
- WebSocket Service `WebsocketService` for real-time updates from the backend.

### WebSocket Endpoint

- Backend: Add a WebSocket endpoint (e.g., `ws/updates`) in Quarkus to broadcast updates (e.g., new weapon added).
- Frontend: Use `WebsocketService` to connect to `ws/updates`, refresh lists (e.g., weapons), and show `MatSnackBar` notifications on updates.

### Additional Tasks

- Ensure responsive design with CSS Grid or Flexbox.
- Add ARIA labels for accessibility. (optional)

### Evaluation Criteria

- Correct routing with a not found page.
- Functional components using Angular Material.
- Complete services for CRUD and API calls.
- WebSocket integration for real-time updates.
- Clean, responsive UI with feedback mechanisms.
