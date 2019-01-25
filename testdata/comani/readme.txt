The commits in the directories "coreboot" and "linux" are the same as those in the "coman" directory, but they were
extracted with the new commands used by the GitCommitExtractor in a recent VM using a newer git version. In order to
reuse the expected result values, the commit numbers, which are longer in the newer git version, are reduced to those
of the older git version. Further, the content of coreboot commit "2a19fb1" is shortened to match the old expected
values, which did not cover the entire commit as it was too long to be counted manually. 