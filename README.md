# Web eID Spring Boot example

![European Regional Development Fund](https://github.com/e-gov/RIHA-Frontend/raw/master/logo/EU/EU.png)

Example Spring Boot web application that uses Web eID for strong authentication
and digital signing with electronic ID smart cards.

## Setup

Note that although the browser considers localhost to be a secure context, it does not have a certificate,
therefore you need to use e.g. Ngrok.io for local testing.

Download ngrok and run locally using two parameters:
```sh
ngrok http 8080
```

## Configuration

Open application.yaml file and set up the `local-origin` and `fingerprint` fields:

- The `local-origin` field must contain the url, which was generated by ngrok.
- The `fingerprint` field must contain the SHA256 fingerprint of the certificate, which you can retrieve from the browser by navigating to the ngrok url and exploring the server certificate details. Here is a [quick guide](https://www.globalsign.com/en/blog/how-to-view-ssl-certificate-details) and for different browsers.

```yaml
spring:
  profiles: dev
  servlet:
    multipart:
      max-file-size: 5000KB
      max-request-size: 5000KB
token:
  validation:
    local-origin: "https://ade0973a6557.ngrok.io"
    fingerprint: "11:D8:AE:60:EC:19:10:C7:94:D7:4C:82:C8:0D:96:B2:07:88:B5:6A:D2:65:FF:F9:B5:14:C8:75:F7:90:08:E1"
    keystore-password: "changeit"
```

Additionally, you can set up the keystore password, and the maximum size of the files to be uploaded.

## Running

Once the `local-origin` and `fingerprint` fields are set, you can run the application with the following command:
```sh
./mvnw spring-boot:run
```
and then navigate the browser to the url, that was generated by ngrok.


## Testing

In case you don't have the Web eID extension installed or want to test `web-eid.js` message handling manually,
you can use the browser developer tools to mock the authentication response from the extension with
`window.postMessage()` as follows:

1. Click *Authenticate*
2. Paste the following snippet into developer tools console:

```js
var loginResponse = await fetch("/auth/login", {
    method: "POST",
    headers: {
      'Content-Type': 'application/json'
    },
    body: '{"auth-token":"eyJhbGciOiJFUzM4NCIsInR5cCI6IkpXVCIsIng1YyI6WyJNSUlFQXpDQ0EyV2dBd0lCQWdJUU9Xa0JXWE5ESm0xYnlGZDNYc1drdmpBS0JnZ3Foa2pPUFFRREJEQmdNUXN3Q1FZRFZRUUdFd0pGUlRFYk1Ca0dBMVVFQ2d3U1Uwc2dTVVFnVTI5c2RYUnBiMjV6SUVGVE1SY3dGUVlEVlFSaERBNU9WRkpGUlMweE1EYzBOekF4TXpFYk1Ca0dBMVVFQXd3U1ZFVlRWQ0J2WmlCRlUxUkZTVVF5TURFNE1CNFhEVEU0TVRBeE9EQTVOVEEwTjFvWERUSXpNVEF4TnpJeE5UazFPVm93ZnpFTE1Ba0dBMVVFQmhNQ1JVVXhLakFvQmdOVkJBTU1JVXJEbFVWUFVrY3NTa0ZCU3kxTFVrbFRWRXBCVGl3ek9EQXdNVEE0TlRjeE9ERVFNQTRHQTFVRUJBd0hTc09WUlU5U1J6RVdNQlFHQTFVRUtnd05Ta0ZCU3kxTFVrbFRWRXBCVGpFYU1CZ0dBMVVFQlJNUlVFNVBSVVV0TXpnd01ERXdPRFUzTVRnd2RqQVFCZ2NxaGtqT1BRSUJCZ1VyZ1FRQUlnTmlBQVI1azFsWHp2U2VJOU8vMXMxcFp2amhFVzhuSXRKb0cwRUJGeG1MRVk2UzdraTF2RjJRM1RFRHg2ZE56dEkxWHR4OTZjczhyNHpZVHdkaVFvRGc3azNkaVV1UjluVFdHeFFFTU8xRkRvNFk5ZkFtaVBHV1QrK0d1T1ZvWlFZM1h4aWpnZ0hETUlJQnZ6QUpCZ05WSFJNRUFqQUFNQTRHQTFVZER3RUIvd1FFQXdJRGlEQkhCZ05WSFNBRVFEQStNRElHQ3lzR0FRUUJnNUVoQVFJQk1DTXdJUVlJS3dZQkJRVUhBZ0VXRldoMGRIQnpPaTh2ZDNkM0xuTnJMbVZsTDBOUVV6QUlCZ1lFQUk5NkFRSXdId1lEVlIwUkJCZ3dGb0VVTXpnd01ERXdPRFUzTVRoQVpXVnpkR2t1WldVd0hRWURWUjBPQkJZRUZPUXN2VFFKRUJWTU1TbWh5Wlg1YmliWUp1YkFNR0VHQ0NzR0FRVUZCd0VEQkZVd1V6QlJCZ1lFQUk1R0FRVXdSekJGRmo5b2RIUndjem92TDNOckxtVmxMMlZ1TDNKbGNHOXphWFJ2Y25rdlkyOXVaR2wwYVc5dWN5MW1iM0l0ZFhObExXOW1MV05sY25ScFptbGpZWFJsY3k4VEFrVk9NQ0FHQTFVZEpRRUIvd1FXTUJRR0NDc0dBUVVGQndNQ0JnZ3JCZ0VGQlFjREJEQWZCZ05WSFNNRUdEQVdnQlRBaEprcHhFNmZPd0kwOXBuaENsWUFDQ2srZXpCekJnZ3JCZ0VGQlFjQkFRUm5NR1V3TEFZSUt3WUJCUVVITUFHR0lHaDBkSEE2THk5aGFXRXVaR1Z0Ynk1emF5NWxaUzlsYzNSbGFXUXlNREU0TURVR0NDc0dBUVVGQnpBQ2hpbG9kSFJ3T2k4dll5NXpheTVsWlM5VVpYTjBYMjltWDBWVFZFVkpSREl3TVRndVpHVnlMbU55ZERBS0JnZ3Foa2pPUFFRREJBT0Jpd0F3Z1ljQ1FnSDFVc21NZHRMWnRpNTFGcTJRUjR3VWtBd3BzbmhzQlYySFFxVVhGWUJKN0VYbkxDa2FYamRaS2tIcEFCZk0wUUV4N1VVaGFJNGk1M2ppSjdFMVk3V09BQUpCRFg0ejYxcG5pSEphcEkxYmtNSWlKUS90aTdoYThmZEpTTVNwQWRzNUN5SEl5SGtReldsVnk4NmY5bUE3RXUzb1JPLzFxK2VGVXpEYk5OM1Z2eTdnUVdRPSJdfQ.eyJhdWQiOlsiaHR0cHM6Ly9yaWEuZWUiLCJ1cm46Y2VydDpzaGEtMjU2OjZmMGRmMjQ0ZTRhODU2Yjk0YjNiM2I0NzU4MmEwYTUxYTMyZDY3NGRiYzcxMDcyMTFlZDIzZDRiZWM2ZDljNzIiXSwiZXhwIjoiMTU4Njg3MTE2OSIsImlhdCI6IjE1ODY4NzA4NjkiLCJpc3MiOiJ3ZWItZWlkIGFwcCB2MC45LjAtMS1nZTZlODlmYSIsIm5vbmNlIjoiMTIzNDU2NzgxMjM0NTY3ODEyMzQ1Njc4MTIzNDU2NzgiLCJzdWIiOiJKw5VFT1JHLEpBQUstS1JJU1RKQU4sMzgwMDEwODU3MTgifQ.0Y5CdMiSZ14rOnd7sbp-XeBQ7qrJVd21yTmAbiRnzAXtwqW8ZROg4jL4J7bpQ2fwyUz4-dVwLoVRVnxfJY82b8NXuxXrDb-8MXXmVYrMW0q0kPbEzqFbEnPYHjNnKAN0"}'
});

window.postMessage({
    action: webeid.Action.AUTHENTICATE_SUCCESS,
    response: {
        status: 200,
        headers: {},
        body: await loginResponse.json()
    }
});
```

## Testing with curl

```shell script
$ curl 'http://localhost:8080/auth/login' -H 'Content-type: application/json' --data-raw '{"auth-token":"eyJhbGciOiJFUzM4NCIsInR5cCI6IkpXVCIsIng1YyI6WyJNSUlFQXpDQ0EyV2dBd0lCQWdJUU9Xa0JXWE5ESm0xYnlGZDNYc1drdmpBS0JnZ3Foa2pPUFFRREJEQmdNUXN3Q1FZRFZRUUdFd0pGUlRFYk1Ca0dBMVVFQ2d3U1Uwc2dTVVFnVTI5c2RYUnBiMjV6SUVGVE1SY3dGUVlEVlFSaERBNU9WRkpGUlMweE1EYzBOekF4TXpFYk1Ca0dBMVVFQXd3U1ZFVlRWQ0J2WmlCRlUxUkZTVVF5TURFNE1CNFhEVEU0TVRBeE9EQTVOVEEwTjFvWERUSXpNVEF4TnpJeE5UazFPVm93ZnpFTE1Ba0dBMVVFQmhNQ1JVVXhLakFvQmdOVkJBTU1JVXJEbFVWUFVrY3NTa0ZCU3kxTFVrbFRWRXBCVGl3ek9EQXdNVEE0TlRjeE9ERVFNQTRHQTFVRUJBd0hTc09WUlU5U1J6RVdNQlFHQTFVRUtnd05Ta0ZCU3kxTFVrbFRWRXBCVGpFYU1CZ0dBMVVFQlJNUlVFNVBSVVV0TXpnd01ERXdPRFUzTVRnd2RqQVFCZ2NxaGtqT1BRSUJCZ1VyZ1FRQUlnTmlBQVI1azFsWHp2U2VJOU8vMXMxcFp2amhFVzhuSXRKb0cwRUJGeG1MRVk2UzdraTF2RjJRM1RFRHg2ZE56dEkxWHR4OTZjczhyNHpZVHdkaVFvRGc3azNkaVV1UjluVFdHeFFFTU8xRkRvNFk5ZkFtaVBHV1QrK0d1T1ZvWlFZM1h4aWpnZ0hETUlJQnZ6QUpCZ05WSFJNRUFqQUFNQTRHQTFVZER3RUIvd1FFQXdJRGlEQkhCZ05WSFNBRVFEQStNRElHQ3lzR0FRUUJnNUVoQVFJQk1DTXdJUVlJS3dZQkJRVUhBZ0VXRldoMGRIQnpPaTh2ZDNkM0xuTnJMbVZsTDBOUVV6QUlCZ1lFQUk5NkFRSXdId1lEVlIwUkJCZ3dGb0VVTXpnd01ERXdPRFUzTVRoQVpXVnpkR2t1WldVd0hRWURWUjBPQkJZRUZPUXN2VFFKRUJWTU1TbWh5Wlg1YmliWUp1YkFNR0VHQ0NzR0FRVUZCd0VEQkZVd1V6QlJCZ1lFQUk1R0FRVXdSekJGRmo5b2RIUndjem92TDNOckxtVmxMMlZ1TDNKbGNHOXphWFJ2Y25rdlkyOXVaR2wwYVc5dWN5MW1iM0l0ZFhObExXOW1MV05sY25ScFptbGpZWFJsY3k4VEFrVk9NQ0FHQTFVZEpRRUIvd1FXTUJRR0NDc0dBUVVGQndNQ0JnZ3JCZ0VGQlFjREJEQWZCZ05WSFNNRUdEQVdnQlRBaEprcHhFNmZPd0kwOXBuaENsWUFDQ2srZXpCekJnZ3JCZ0VGQlFjQkFRUm5NR1V3TEFZSUt3WUJCUVVITUFHR0lHaDBkSEE2THk5aGFXRXVaR1Z0Ynk1emF5NWxaUzlsYzNSbGFXUXlNREU0TURVR0NDc0dBUVVGQnpBQ2hpbG9kSFJ3T2k4dll5NXpheTVsWlM5VVpYTjBYMjltWDBWVFZFVkpSREl3TVRndVpHVnlMbU55ZERBS0JnZ3Foa2pPUFFRREJBT0Jpd0F3Z1ljQ1FnSDFVc21NZHRMWnRpNTFGcTJRUjR3VWtBd3BzbmhzQlYySFFxVVhGWUJKN0VYbkxDa2FYamRaS2tIcEFCZk0wUUV4N1VVaGFJNGk1M2ppSjdFMVk3V09BQUpCRFg0ejYxcG5pSEphcEkxYmtNSWlKUS90aTdoYThmZEpTTVNwQWRzNUN5SEl5SGtReldsVnk4NmY5bUE3RXUzb1JPLzFxK2VGVXpEYk5OM1Z2eTdnUVdRPSJdfQ.eyJhdWQiOlsiaHR0cHM6Ly9yaWEuZWUiLCJ1cm46Y2VydDpzaGEtMjU2OjZmMGRmMjQ0ZTRhODU2Yjk0YjNiM2I0NzU4MmEwYTUxYTMyZDY3NGRiYzcxMDcyMTFlZDIzZDRiZWM2ZDljNzIiXSwiZXhwIjoiMTU4Njg3MTE2OSIsImlhdCI6IjE1ODY4NzA4NjkiLCJpc3MiOiJ3ZWItZWlkIGFwcCB2MC45LjAtMS1nZTZlODlmYSIsIm5vbmNlIjoiMTIzNDU2NzgxMjM0NTY3ODEyMzQ1Njc4MTIzNDU2NzgiLCJzdWIiOiJKw5VFT1JHLEpBQUstS1JJU1RKQU4sMzgwMDEwODU3MTgifQ.0Y5CdMiSZ14rOnd7sbp-XeBQ7qrJVd21yTmAbiRnzAXtwqW8ZROg4jL4J7bpQ2fwyUz4-dVwLoVRVnxfJY82b8NXuxXrDb-8MXXmVYrMW0q0kPbEzqFbEnPYHjNnKAN0"}'
{"sub":"JÕEORG,JAAK-KRISTJAN,38001085718","auth":["ROLE_USER"]}
```

## Formatting code

We use *prettier-java* for formatting code, install and run it as follows:

```shell script
npm install -g prettier prettier-plugin-java
prettier --write "**/*.{java,js,html}"
```

## Deployment

Build Docker image with Jib as follows:

```sh
mvn com.google.cloud.tools:jib-maven-plugin:dockerBuild
```

and deploy with Docker Compose as follows:

```sh
docker-compose up -d
```
