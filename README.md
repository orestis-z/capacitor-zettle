# capacitor-zettle

The "ZettlePayment" plugin integrates Zettle payments into Capacitor apps, enabling card transactions with minimal setup. It offers a straightforward JavaScript API for easy payment processing and management.

## Install

```bash
npm install capacitor-zettle
npx cap sync
```

### Android

```gradle
buildscript {

    repositories {
        google()
        mavenCentral()

        // ADD THIS
        maven {
            url = uri("https://maven.pkg.github.com/iZettle/sdk-android")
            credentials(HttpHeaderCredentials) {
                name "Authorization"
                value "Bearer $project.githubToken"
            }
            authentication {
                header(HttpHeaderAuthentication)
            }
        }
    }
    // ...
}

allprojects {
    repositories {
        google()
        mavenCentral()

        // ADD THIS
        maven {
            url = uri("https://maven.pkg.github.com/iZettle/sdk-android")
            credentials(HttpHeaderCredentials) {
                name "Authorization"
                value "Bearer $project.githubToken"
            }
            authentication {
                header(HttpHeaderAuthentication)
            }
        }
    }
}
```

## API

<docgen-index>

* [`initialize(...)`](#initialize)
* [`initiatePayment(...)`](#initiatepayment)
* [`initiateRefund(...)`](#initiaterefund)
* [`showCardReaderSettings()`](#showcardreadersettings)

</docgen-index>

<docgen-api>
<!--Update the source file JSDoc comments and rerun docgen to update the docs below-->

### initialize(...)

```typescript
initialize(options: { devMode: boolean; }) => Promise<any>
```

| Param         | Type                               |
| ------------- | ---------------------------------- |
| **`options`** | <code>{ devMode: boolean; }</code> |

**Returns:** <code>Promise&lt;any&gt;</code>

--------------------


### initiatePayment(...)

```typescript
initiatePayment(options: { amount: number; currency: string; }) => Promise<any>
```

| Param         | Type                                               |
| ------------- | -------------------------------------------------- |
| **`options`** | <code>{ amount: number; currency: string; }</code> |

**Returns:** <code>Promise&lt;any&gt;</code>

--------------------


### initiateRefund(...)

```typescript
initiateRefund(options: { amount: number; taxAmount: number; receiptNumber: string; }) => Promise<any>
```

| Param         | Type                                                                       |
| ------------- | -------------------------------------------------------------------------- |
| **`options`** | <code>{ amount: number; taxAmount: number; receiptNumber: string; }</code> |

**Returns:** <code>Promise&lt;any&gt;</code>

--------------------


### showCardReaderSettings()

```typescript
showCardReaderSettings() => Promise<any>
```

**Returns:** <code>Promise&lt;any&gt;</code>

--------------------

</docgen-api>
