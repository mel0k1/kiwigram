# kiwigram

[![CI](https://github.com/mel0k1/kiwigram/actions/workflows/release.yml/badge.svg)](https://github.com/mel0k1/kiwigram/actions/workflows/release.yml)
[![License](https://img.shields.io/badge/license-GPL--3.0-blue.svg)](LICENSE)

Android Telegram client with a plugin permission model, built on [exteraless](https://github.com/exteraless/exteraless). The long-term goal of the fork is a Material 3 Expressive redesign of the interface.

exteraless is an open-source implementation of exteraGram features on top of [NagramX](https://github.com/risin42/NagramX). kiwigram continues that work as its own build:

* Package name: `app.kiwigram`
* Plugin system with a permission model: a plugin declares what it needs, the install sheet shows it, and it gets only what was declared
* Python plugin engine (Chaquopy) with an isolated runtime
* Custom icon packs, Pill Stack, side menu and chat appearance settings
* 64-bit only (`arm64-v8a`, `x86_64`): Chaquopy ships Python 3.11 for these ABIs

## Build

Requirements: JDK 21, Python 3.11 on the build machine (for the Chaquopy runtime step), Android SDK with NDK 27.2.

1. Clone with submodules:

    ```bash
    git clone --recursive --shallow-submodules https://github.com/mel0k1/kiwigram.git kiwigram
    ```

    If the repository is already cloned:

    ```bash
    git submodule update --init --recursive --depth=1
    ```

2. Optional `local.properties` in the project root:

    ```properties
    TELEGRAM_APP_ID=<your_app_id>
    TELEGRAM_APP_HASH=<your_app_hash>
    MAPS_API_KEY=<your_maps_key>
    KEYSTORE_PASS=<store_password>
    ALIAS_NAME=<key_alias>
    ALIAS_PASS=<key_password>
    ```

   Without these the build compiles against the bundled Telegram test API keys and signs with the Android debug key. For release signing put your `TMessagesProj/release.keystore` next to the build file.

3. Build:

    ```bash
    ./gradlew :TMessagesProj:assembleDebug
    ```

`NATIVE_TARGET` selects the ABI set: `arm64-v8a`, `x86_64`, `universal` (both), or `SKIP` (Java and resources only, no native code).

## CI

| Trigger | Job |
|---|---|
| Push to `main` | Release build (`universal`), APK attached to an auto pre-release tagged `ci-<sha>` (keeps the last five) |
| Pull request | Debug build (`arm64-v8a`), APK in run artifacts |
| Manual dispatch | Same as the triggering workflow |

Optional repository secrets, both base64-encoded:

* `LOCAL_PROPERTIES` — the contents of `local.properties` (`base64 -w0 local.properties`)
* `RELEASE_KEYSTORE` — the release keystore (`base64 -w0 TMessagesProj/release.keystore`)

Without the secrets, builds still succeed with the built-in test API keys and the debug signing key. Telegram-channel publishing needs `TG_BOT_TOKEN`, `TG_API_ID`, `TG_API_HASH`, `TG_CHAT_ID` and stays off otherwise.

## Development

The plugin catalog test suite runs on plain CPython, no device needed:

```bash
python3 -m pytest tests/ -q
```

## Roadmap

* Material 3 Expressive redesign: shape language, expressive motion, color roles on top of the existing theme system
* Own app icon and branding assets (the current launcher icon is inherited)

## Credits

Built on the work of many projects: [Telegram for Android](https://github.com/DrKLO/Telegram), [exteraless](https://github.com/exteraless/exteraless), [Nagram](https://github.com/NextAlone/Nagram), [NagramX](https://github.com/risin42/NagramX), [Nekogram](https://github.com/Nekogram/Nekogram), [exteraGram](https://github.com/exteraSquad/exteraGram), [AyuGram](https://github.com/AyuGram/AyuGram4A), [Cherrygram](https://github.com/arsLan4k1390/Cherrygram), [OctoGram](https://github.com/OctoGramApp/OctoGram).

Icons and visual design inherited from exteraGram are the work of [@the8055u](https://t.me/the8055u) and the [@BlueprintDsgn](https://t.me/BlueprintDsgn) studio. Rights to those materials belong to their authors.

## License

GPL-3.0, same as upstream. See [LICENSE](LICENSE).

---

## Русский

**kiwigram** — форк [exteraless](https://github.com/exteraless/exteraless): открытая реализация возможностей exteraGram поверх [NagramX](https://github.com/risin42/NagramX). Долгосрочная цель форка — редизайн интерфейса в стиле Material 3 Expressive.

* Имя пакета: `app.kiwigram`
* Плагины с моделью разрешений: плагин декларирует, что ему нужно, это видно на экране установки, и получает он только заявленное
* Python-движок плагинов (Chaquopy) с изолированным рантаймом
* Иконпаки, Pill Stack, боковое меню и настройки внешнего вида чатов
* Только 64-битные ABI (`arm64-v8a`, `x86_64`): Chaquopy собирает Python 3.11 лишь под них

### Сборка

Нужны JDK 21, Python 3.11 на машине сборки и Android SDK с NDK 27.2.

```bash
git clone --recursive --shallow-submodules https://github.com/mel0k1/kiwigram.git kiwigram
./gradlew :TMessagesProj:assembleDebug
```

`local.properties` в корне проекта: `TELEGRAM_APP_ID`, `TELEGRAM_APP_HASH` (свои ключи с [my.telegram.org](https://my.telegram.org/auth); по умолчанию вкомпилированы тестовые), `MAPS_API_KEY`, для подписи — `KEYSTORE_PASS`, `ALIAS_NAME`, `ALIAS_PASS` и `TMessagesProj/release.keystore`. `NATIVE_TARGET` задаёт набор ABI: `arm64-v8a`, `x86_64`, `universal` или `SKIP`.

### CI

Пуш в `main` — release-сборка с автоматическим pre-release `ci-<sha>`; pull request — debug-сборка с APK в артефактах. Секреты `LOCAL_PROPERTIES` и `RELEASE_KEYSTORE` (оба в base64) опциональны: без них сборка идёт на тестовых ключах и отладочной подписи.

### Тесты

```bash
python3 -m pytest tests/ -q
```

### Благодарности

Сделано на базе множества проектов: [Telegram for Android](https://github.com/DrKLO/Telegram), [exteraless](https://github.com/exteraless/exteraless), [Nagram](https://github.com/NextAlone/Nagram), [NagramX](https://github.com/risin42/NagramX), [Nekogram](https://github.com/Nekogram/Nekogram), [exteraGram](https://github.com/exteraSquad/exteraGram), [AyuGram](https://github.com/AyuGram/AyuGram4A), [Cherrygram](https://github.com/arsLan4k1390/Cherrygram), [OctoGram](https://github.com/OctoGramApp/OctoGram).

Иконки и оформление, унаследованные от exteraGram, созданы [@the8055u](https://t.me/the8055u) и студией [@BlueprintDsgn](https://t.me/BlueprintDsgn). Права на эти материалы принадлежат авторам.

### Лицензия

GPL-3.0, как и у апстрима. Подробности в [LICENSE](LICENSE).
