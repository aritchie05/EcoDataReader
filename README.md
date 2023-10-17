# Eco Data Reader

> Warning: This project is very much a work in progress and many processes need to be improved and formalized.

The main purpose of this project is to read items and recipes from the Eco server game files and convert the objects
into strings that can be used in the [EcoCraftingTool](https://github.com/aritchie05/EcoCraftingTool) 
`items.ts` and `recipes.ts`.

It also has code to parse the `defaultstrings.csv` file that can be downloaded from the 
[Eco Crowdin](https://crowdin.com/translate/eco-by-strange-loop-games)
translations site. It can convert the csv file into a `LocaleData` object that the EcoCraftingTool can use.

## Initial Setup

### Eco Game Files
To configure the project, download the most recent version of the Eco Server from the 
[Eco website](https://play.eco/account).

Unzip the project and store it somewhere memorable. Navigate to the `Mods\__core__` folder and then copy the full path from here.
For example:

`D:\Eco Servers\EcoServerPC_v0.10.0.0-beta-staging-2770\Mods\__core__`

This is the base directory that the project will look in when it is gathering all the game files needed to get all the items and recipes.

Then, go to `src/main/java/com/apex/Main.java` and paste this path in the `ECO_SERVER_PATH` constant.

### Translations
Go to [Eco Crowdin](https://crowdin.com/translate/eco-by-strange-loop-games). Select any language and then open `defaultstrings.csv`.
Then, click the top left drawer icon and go to File | Download. Save this file in `src/main/resources/locale/defaultstrings.csv`

## Using the Project
Use `generateNewItemsString()` and `generateNewRecipesString()` methods to generate new entries for `items.ts` and `recipes.ts` respectively.
Note that this will generate any items or recipes that are not contained within the `current-items.txt` and `current-recipes.txt` files within the
resources folder.

Use `getLocaleJson()` to generate the entire `LocaleData[]` object from the `defaultstrings.csv` file. 

When copying the locale data over to the crafting tool, be sure to include these entries at the end which are not produced by this project.

```json
        {
  'id': 'NoUpgradeBasic',
  'en': 'No Upgrade',
  'fr': 'Pas de mise à niveau',
  'es': 'Sin actualización',
  'de': 'Kein Upgrade',
  'pt': 'Sem Melhoria',
  'it': 'Nessun Aggiornamento',
  'tr': 'Geliştirme Yok',
  'pl': 'Bez aktualizacji',
  'ru': 'Без обновления',
  'uk': 'Без оновлення',
  'ko': '업그레이드 없음',
  'zh': '无升级',
  'ja': 'アップグレードなし'
},
{
'id': 'NoUpgradeAdvanced',
'en': 'No Upgrade',
'fr': 'Pas de mise à niveau',
'es': 'Sin actualización',
'de': 'Kein Upgrade',
'pt': 'Sem Melhoria',
'it': 'Nessun Aggiornamento',
'tr': 'Geliştirme Yok',
'pl': 'Bez aktualizacji',
'ru': 'Без обновления',
'uk': 'Без оновлення',
'ko': '업그레이드 없음',
'zh': '无升级',
'ja': 'アップグレードなし'
},
{
'id': 'NoUpgradeModern',
'en': 'No Upgrade',
'fr': 'Pas de mise à niveau',
'es': 'Sin actualización',
'de': 'Kein Upgrade',
'pt': 'Sem Melhoria',
'it': 'Nessun Aggiornamento',
'tr': 'Geliştirme Yok',
'pl': 'Bez aktualizacji',
'ru': 'Без обновления',
'uk': 'Без оновлення',
'ko': '업그레이드 없음',
'zh': '无升级',
'ja': 'アップグレードなし'
},
{
'id': 'NoUpgradeNone',
'en': 'No Upgrade',
'fr': 'Pas de mise à niveau',
'es': 'Sin actualización',
'de': 'Kein Upgrade',
'pt': 'Sem Melhoria',
'it': 'Nessun Aggiornamento',
'tr': 'Geliştirme Yok',
'pl': 'Bez aktualizacji',
'ru': 'Без обновления',
'uk': 'Без оновлення',
'ko': '업그레이드 없음',
'zh': '无升级',
'ja': 'アップグレードなし'
},
{
'id': 'SpecializedAdvancedUpgrade',
'en': 'Specialized Advanced Upgrade',
'fr': 'Mise à niveau avancée spécialisée',
'es': 'Actualización avanzada especializada',
'de': 'Spezialisiertes erweitertes Upgrade',
'pt': 'Melhoria Avançada Especializada',
'it': 'Aggiornamento Avanzato Specializzato',
'tr': 'Uzmanlaşmış Seviye Geliştirme',
'pl': 'Specjalistyczne zaawansowane ulepszenie',
'ru': 'Специализированное расширенное обновление',
'uk': 'Спеціалізоване розширене оновлення',
'ko': '전문화된 고급 업그레이드',
'zh': '专业进阶升级',
'ja': '専門の高度なアップグレード'
},
{
'id': 'SpecializedBasicUpgrade',
'en': 'Specialized Basic Upgrade',
'fr': 'Mise à niveau de base spécialisée',
'es': 'Actualización básica especializada',
'de': 'Spezialisiertes Basis-Upgrade',
'pt': 'Melhoria Básica Especializada',
'it': 'Aggiornamento Base Specializzato',
'tr': 'Uzmanlaşmış Temel Geliştirme',
'pl': 'Specjalistyczne podstawowe ulepszenie',
'ru': 'Специализированное базовое обновление',
'uk': 'Спеціалізоване базове оновлення',
'ko': '전문화된 기본 업그레이드',
'zh': '专业基础升级',
'ja': '専門の基本アップグレード'
},
{
'id': 'SpecializedModernUpgrade',
'en': 'Specialized Modern Upgrade',
'fr': 'Mise à niveau moderne spécialisée',
'es': 'Actualización moderna especializada',
'de': 'Spezialisiertes modernes Upgrade',
'pt': 'Melhoria Moderna Especializada',
'it': 'Aggiornamento Moderno Specializzato',
'tr': 'Uzmanlaşmış Modern Geliştirme',
'pl': 'Specjalistyczne nowoczesne ulepszenie',
'ru': 'Специализированное современное обновление',
'uk': 'Спеціалізоване сучасне оновлення',
'ko': '전문화된 현대 업그레이드',
'zh': '专业现代升级',
'ja': 'スペシャライズドモダンアップグレード'
}

```
