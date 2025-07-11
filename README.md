# MarkdownApp
## Приложение для загрузки, редактирования и просмотра Markdown-документа с базовой поддержкой форматирования
Поддерживаются конструкции:
1. Заголовки уровней 1-6,
2. Жирный текст,
3. Курсивный текст,
4. Зачеркнутый текст,
5. Таблицы,
6. Изображения (по ссылке).
## Используемые технологии
1. **Инъекция зависимостей**: ручная
2. **Работа с сетью**: [java.net](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/net/package-summary.html)
3. **Многопоточность**: [kotlin.concurrent](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.concurrent/thread.html)
## Идея
Идея состоит в преобразовании строки в лес (граф из нескольких деревьев) элементов Markdown с последующим преобразованием леса во View.
## Некоторые факты
1. Для скачивания изображений используется [Thread](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.concurrent/thread.html). Возможно, лучше использовать [ExecutorService](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/concurrent/ExecutorService.html), чтобы контролировать количество используемых потоков (в документе может оказаться много изображений, их одновременное скачивание может привести к неприятным последствиям).
2. Можно было попробовать не строить лес, который может состоять из достаточно больших деревьев, а строить "плоский" лес, состоящий из небольших деревьев, причём элементы размещать в [RecyclerView](https://developer.android.google.cn/reference/kotlin/androidx/recyclerview/widget/RecyclerView). Это позволило бы уменьшить вложенность View. Однако текущий подход позволяет (теоретически) реализовать возможность сворачивать текст, относящийся к заголовку, в режиме просмотра документа.
3. Используется самописный кеш, хотя можно было попробовать воспользоваться кешем из [android.util](https://developer.android.com/reference/android/util/LruCache).
## Примеры
Можно рассмотреть работу приложения на следующих примерах:
1. https://raw.githubusercontent.com/Wolfram158/MarkdownApp/main/README.md
2. https://raw.githubusercontent.com/Wolfram158/MarkdownApp/main/samples/1.md
3. https://raw.githubusercontent.com/Wolfram158/MarkdownApp/main/samples/2.md
4. https://raw.githubusercontent.com/yandex/yatagan/refs/heads/main/README.md
