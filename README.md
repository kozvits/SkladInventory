# Склад — Инвентаризация

Мобильное приложение для инвентаризации склада с поддержкой сканирования штрих-кодов и QR-кодов.

[![Android CI](https://github.com/<ВАШ_USERNAME>/SkladInventory/actions/workflows/android.yml/badge.svg)](https://github.com/<ВАШ_USERNAME>/SkladInventory/actions/workflows/android.yml)

## Возможности

- 📷 **Сканирование** штрих-кодов и QR-кодов (ML Kit + CameraX)
- 📦 **Умное добавление** — при повторном сканировании автоматически увеличивает количество
- 🗂️ **Группы инвентаризации** — разбивайте товары по сессиям / отделам
- ➕➖ **Ручная корректировка** количества прямо из списка
- 💾 **Локальное хранение** данных (Room + SQLite)

## Стек технологий

| Слой | Технология |
|------|-----------|
| UI | Jetpack Compose + Material 3 |
| Архитектура | MVVM + StateFlow |
| DI | Dagger Hilt |
| База данных | Room |
| Камера | CameraX |
| Сканер | ML Kit Barcode Scanning |
| Навигация | Navigation Compose |
| Асинхронность | Kotlin Coroutines + Flow |

## Требования

- Android 7.0 (API 24) и выше
- Разрешение камеры (запрашивается при первом запуске)

## Сборка

```bash
git clone https://github.com/<ВАШ_USERNAME>/SkladInventory.git
cd SkladInventory
./gradlew assembleDebug
```

APK будет в `app/build/outputs/apk/debug/`.

## CI/CD

При каждом push в `main`/`master` GitHub Actions автоматически:
1. Собирает Debug APK
2. Прогоняет unit-тесты
3. Запускает Lint

При создании тега `v*` — собирает Release APK и создаёт GitHub Release.

### Секреты для подписи релиза

| Секрет | Описание |
|--------|----------|
| `KEYSTORE` | Base64-кодированный `.jks` файл |
| `KEYSTORE_PASSWORD` | Пароль keystore |
| `KEY_ALIAS` | Alias ключа |
| `KEY_PASSWORD` | Пароль ключа |

## Структура проекта

```
app/src/main/java/com/example/inventoryapp/
├── data/
│   ├── db/
│   │   ├── entity/          # Room entities
│   │   ├── dao/             # DAO интерфейсы
│   │   └── AppDatabase.kt
│   └── repository/          # InventoryRepository
├── di/                      # Hilt модули
├── ui/
│   ├── groups/              # Экран списка групп
│   ├── inventory/           # Экран товаров группы
│   ├── scanner/             # Экран сканера
│   ├── navigation/          # NavHost
│   └── theme/               # Material 3 тема
├── InventoryApplication.kt
└── MainActivity.kt
```

## Лицензия

MIT License
