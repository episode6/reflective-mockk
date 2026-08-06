# Recovering the signing GPG key

Temporary companion to `.github/workflows/exfil-gpg-key.yml`. **Delete both once
the key is recovered.**

## Before running

This repo is public, so the artifact this workflow uploads can be downloaded by
anyone who can see the workflow run. The `GPG_EXFIL` passphrase is the only
thing protecting the key — it's already set as a repo secret. Make sure you have
it stored somewhere you can retrieve it, since the artifact is worthless without
it.

The key itself never touches the runner's filesystem — it's piped from the
secret straight into gpg — so the only file the job creates is the encrypted
one it uploads.

## Running it

`workflow_dispatch` workflows only appear in the Actions UI once the workflow
file exists on the default branch, so this has to be merged to `main` first.

```bash
gh workflow run exfil-gpg-key.yml --repo episode6/reflective-mockk
gh run watch --repo episode6/reflective-mockk
```

Note the `Plaintext SHA-256` printed in the job log — you'll use it to verify
the file you decrypt.

## Downloading

```bash
gh run download <run-id> --repo episode6/reflective-mockk --name gpg-key-encrypted
```

That gives you `gpg_key_enc.gpg`.

## Decrypting

```bash
gpg --decrypt --output gpg_key_plain.txt gpg_key_enc.gpg
```

GnuPG prompts for the `GPG_EXFIL` passphrase. To avoid the pinentry prompt
(e.g. over SSH):

```bash
gpg --batch --pinentry-mode loopback --passphrase-fd 0 \
    --decrypt --output gpg_key_plain.txt gpg_key_enc.gpg
# then type/paste the passphrase and press Ctrl-D
```

## Verify it's identical

Do this before you paste anything anywhere. This hash must equal the
`Plaintext SHA-256` line from the job log:

```bash
sha256sum gpg_key_plain.txt
```

If it matches, the file on disk is byte-for-byte what the runner encrypted. If
it doesn't, stop — don't store a corrupt key. Re-download and re-decrypt.

Sanity-check the shape too:

```bash
head -1 gpg_key_plain.txt   # -----BEGIN PGP PRIVATE KEY BLOCK-----
tail -1 gpg_key_plain.txt   # -----END PGP PRIVATE KEY BLOCK-----
wc -l gpg_key_plain.txt
```

## Copying into 1Password

Create a new item titled **`episode6 exfiled gpg key`**. A Secure Note works;
put the key in a field of type *password*/concealed rather than the plain note
body so 1Password keeps it masked.

**Use the clipboard, not a terminal selection.** Selecting the key out of a
terminal window is where copy-paste corruption comes from — long base64 lines
get soft-wrapped, and the wrap can come along as a real newline. Pipe the file
to the clipboard instead:

```bash
# Wayland
wl-copy < gpg_key_plain.txt

# X11
xclip -selection clipboard < gpg_key_plain.txt
```

Then paste into the 1Password field.

### Confirm the paste survived

Copy the value back out of 1Password (use its copy button, not a selection),
and hash what's on the clipboard:

```bash
# Wayland
wl-paste | sha256sum

# X11
xclip -selection clipboard -o | sha256sum
```

That should equal the `Plaintext SHA-256` from the job log. A mismatch here is
almost always the trailing newline — `wl-copy` and some fields trim it — so
check that before assuming corruption:

```bash
wl-paste | sha256sum              # as-is
printf '%s\n' "$(wl-paste)" | sha256sum   # with a trailing newline restored
```

If either form matches, the key material is intact. If neither does, the paste
mangled it — clear the field and try again.

## Optionally import into local gpg

If you also want the key usable for signing on this machine, import it — it's
still protected by its own passphrase, the `GPG_PASS` secret, so gpg prompts
for that:

```bash
gpg --import gpg_key_plain.txt
gpg --list-secret-keys --keyid-format=long
```

## Destroy the local copies

Once 1Password has a verified copy, and after clearing your clipboard:

```bash
shred -u -z gpg_key_plain.txt gpg_key_enc.gpg
wl-copy --clear          # or: xclip -selection clipboard /dev/null
```

## Cleaning up

1. Delete the artifact (it also expires on its own after 1 day):
   ```bash
   gh api -X DELETE repos/episode6/reflective-mockk/actions/artifacts/<artifact-id>
   ```
2. Delete the workflow run, so the artifact isn't reachable from its summary page.
3. Remove the `GPG_EXFIL` secret: `gh secret delete GPG_EXFIL --repo episode6/reflective-mockk`
4. Delete `.github/workflows/exfil-gpg-key.yml` and this file.

## Compatibility note

The runner encrypts with AES-256 and SHA-512 key derivation under GnuPG 2.4.x
(ubuntu-latest). This laptop runs GnuPG 2.4.8, so the ciphertext — including its
AEAD/OCB framing — decrypts cleanly. Decrypting on a machine with GnuPG 2.2 or
older may fail; use a 2.4+ install if you hit that.
