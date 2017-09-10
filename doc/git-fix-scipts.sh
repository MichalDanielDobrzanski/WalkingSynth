# https://stackoverflow.com/questions/4493936/could-i-change-my-name-and-surname-in-all-previous-commits
git filter-branch -f --commit-filter '
OLD_NAME="Michal Dobrzanski"
CORRECT_NAME="Michał Dobrzański"
if [ "$GIT_AUTHOR_NAME" = "$OLD_NAME" ]
then
    export GIT_AUTHOR_NAME="$CORRECT_NAME"
fi
git commit-tree "$@"'

# solution 5:
# https://stackoverflow.com/questions/307828/completely-remove-file-from-all-git-repository-commit-history
# + find your unwanted files
git filter-branch --index-filter
'git rm --cached --ignore-unmatch <file>'