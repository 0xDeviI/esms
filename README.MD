# eSMS

eSMS is an Android application designed to enable secure and encrypted communication over SMS during periods of internet shutdowns in Iran. The project was created in response to repeated restrictions on internet access imposed on Iranian citizens, particularly during times of civil unrest and public demonstrations.

The motivation behind eSMS is to provide a minimal yet practical communication channel when conventional internet-based messaging platforms are unavailable or blocked. The project was designed and implemented collaboratively with a colleague in Iran, whose firsthand accounts of the situation helped shape both the purpose and constraints of the application.

## How It Works

eSMS encrypts messages locally using AES-256 with a user-defined secret key and sends the encrypted payload via standard SMS. If the recipient has eSMS installed on their device, they are notified upon receiving an encrypted message. To read the message, the recipient must enter the correct password to decrypt its contents.

All encryption and decryption operations are performed on the user’s device, and no plaintext message content is transmitted over the network.

## Password Sharing

In the absence of internet access, securely sharing encryption keys is inherently challenging. Passwords should therefore be exchanged in advance or shared only with trusted individuals through secure, offline means. eSMS is intended for use within small, trusted groups rather than for open or public communication.

## Intended Use and Limitations

eSMS is intended for scenarios where internet access is restricted and users need to coordinate or exchange sensitive information, such as locations or updates related to demonstrations. While message contents are encrypted, users should remain aware that SMS metadata and traffic patterns may still be observable. Encrypted messages are transmitted as Base64-encoded data, which may be distinguishable from ordinary SMS traffic.

For this reason, eSMS should be used cautiously and only when necessary, with a clear understanding of the associated risks.

## Vision

eSMS is a small contribution toward preserving the ability to communicate freely under restrictive conditions. It stands as a technical and ethical response to censorship and enforced disconnection, with the hope of supporting a future where free communication is not constrained by political boundaries.
