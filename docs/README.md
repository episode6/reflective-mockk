Get your default answers back!

[![Maven Central](https://img.shields.io/maven-central/v/com.episode6.reflectivemockk/reflective-mockk.svg?style=flat-square)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22com.episode6.reflectivemockk%22)

### Installation

```groovy
dependencies {
  testImplementation("com.episode6.reflectivemockk:reflective-mockk:{{ site.version }}")
}
```

<sub>{{ site.title }} v{{ site.version }} is compiled against Kotlin v{{ site.kotlinVersion }}</sub>

### Usage

**Create a new mockk with a default answer...**

```kotlin
val mock = reflectiveMockk<SomeBuilder> {
  defaultAnswer { self }
}
```

<br/>

**Apply a reflective stubs to an existing mockk...**

```kotlin
val mock = mockk<SomeBuilder>()
mock.reflectiveStubs {
  defaultAnswer { self }
}
```

<br/>

**Only stub methods based on return type...**

```kotlin
val mock = reflectiveMockk<SomeBuilder> {
  answerEveryCallIn(normalMemberFunctions.filterReturnType<SomeBuilder>()) { self }
}
```

<br/>

**Stub methods manually...**

```kotlin
val mock = reflectiveMockk<SomeBuilder> {
  memberFunctions
    .filter { it.returnType.classifier == SomeBuilder::class }
    .filter { !it.isSuspend }
    .forEach { call ->
      everyCallTo(call) returns self
      // OR every { callTo(call) } returns self
    }
}
```

<br/>

**Stub suspend methods...**

```kotlin
val mock = reflectiveMockk<SomeBuilder> {
  coAnswerEveryCallIn(suspendMemberFunctions) { awaitCancellation() }
}
```

<br/>

**Stub suspend methods manually...**

```kotlin
val mock = reflectiveMockk<SomeBuilder> {
  memberFunctions
    .filter { it.isSuspend }
    .forEach { call ->
      coEverySuspendCallTo(call) coAnswers { awaitCancellation() }
      // OR coEvery { suspendCallTo(call) } coAnswers { awaitCancellation() }
    }
}
```

{% include readme_index.html %}

