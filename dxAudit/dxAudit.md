### Friction 1: Mixed Platform Instructions Without Clear Separation (Android Studio vs Xcode) 
- **Link**: [Installation](https://developer.multipaz.org/docs/getting-started/installation/#%EF%B8%8F-some-gotchas-to-be-aware-of-ios-only)
- **Impact**: Meidum — The guide mixes Kotlin code with Xcode project settings without clearly separating them.
- **Proposed Fix**: Add clear headings like “Update Kotlin Code” and “Update Xcode Project Settings” so readers know exactly where each change applies.

### Friction 2: Pending implementations
- **Link**: [Issuer](https://developer.multipaz.org/docs/getting-started/issuer)
- **Impact**: Medium — When the documentation states 'Not yet implemented due to rapid development...', it can create the perception that the whole documentation was rushed.
- **Proposed Fix**: Include a straightforward Work in Progress note accompanied by an icon (🚧).

### Friction 3:TODO: References
- **Link**: [Issuer Trust](https://developer.multipaz.org/docs/getting-started/issuer](https://developer.multipaz.org/docs/getting-started/verifier/issuer-trust))
- **Impact**: High — Having link references for completed items creates an issue: users expecting to follow them will find no destination..
- **Proposed Fix**: Remove the todo and add the link if the link does not exist remove the text: "This was already handled in the holder/reader trust section (todo: link)".
