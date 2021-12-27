#!/usr/bin/env bash

BLUE='\e[34m'
RED='\e[31m'
RESET='\e[m'

info() {
	printf "[${BLUE}INFO${RESET}] %s\n" "$*"
}

error() {
	printf "[${RED}ERROR${RESET}] ${RED}%s${RESET}\n" "$*" >&2
	return 1
}

git_config() {
	local name email
	name="$(gh api --cache 5s "users/$GITHUB_ACTOR" -q '.name//.login')"
	email="$(gh api --cache 5s "users/$GITHUB_ACTOR" -q '.email//"\(.id)+\(.login)@users.noreply.github.com"')"
	[ -z "$name" ] || git config --global user.name "$name"
	[ -z "$email" ] || git config --global user.email "$email"
}

check_version() {
	local VERSION_PATTERN='^\([0-9]\{1,\}\(\.[0-9]\{1,\}\)*\)\(-SNAPSHOT\)\{0,1\}$'
	expr "$1" : "$VERSION_PATTERN" >/dev/null || error "invalid $2 version <$1>"
}

set_property() {
	./mvnw -B --color=always -N versions:set-property -DautoLinkItems=false "-Dproperty=$1" "-DnewVersion=$2"
}

release_prepare() {
	COMMIT_ID="$(git log -1 --pretty=%H)"
	if TAG="$(git describe --exact-match --tags 2>/dev/null)"; then
		info "Tag $TAG found on commit $(git log -1 --pretty=%H), skip creating tag."
		return
	fi
	[ "${1-}" != "--dry-run" ] || DRY_RUN=1
	if [ -n "$INPUT_RELEASE" ]; then
		case "$INPUT_RELEASE" in
		*-SNAPSHOT) error "expect a release revision but got <$INPUT_RELEASE>" ;;
		*) check_version "$INPUT_RELEASE" release ;;
		esac
		REVISION="$INPUT_RELEASE"
	else
		REVISION="$(./mvnw -B help:evaluate -Dexpression=project.version -q -DforceStdout)"
		case "$REVISION" in
		*-SNAPSHOT) REVISION="${REVISION%-SNAPSHOT}" ;;
		*) error "expect a snapshot revision but got <$REVISION>" ;;
		esac
		check_version "$REVISION" release
	fi

	local TAG_PATTERN='^\(v\{0,1\}[0-9]\{1,\}\(\.[0-9]\{1,\}\)*\)$'
	if [ -n "$INPUT_TAG" ]; then
		expr "$INPUT_TAG" : "$TAG_PATTERN" >/dev/null || error "invalid git tag <$INPUT_TAG>"
		TAG="$INPUT_TAG"
	else
		TAG="$REVISION"
	fi
	case "$INPUT_NEXT_VERSION" in
	"")
		case "$REVISION" in
		*.*)
			PREFIX="${REVISION%.*}."
			SUFFIX="${REVISION##*.}"
			;;
		*)
			PREFIX=
			SUFFIX="$REVISION"
			;;
		esac
		[ "$((SUFFIX * 1))" = "$SUFFIX" ] 2>/dev/null || error "fail to calculate next snapshot on revision <$REVISION>"
		NEXT_SNAPSHOT="$PREFIX$((SUFFIX + 1))-SNAPSHOT"
		;;
	*-SNAPSHOT)
		check_version "$INPUT_NEXT_VERSION" snapshot
		NEXT_SNAPSHOT="$INPUT_NEXT_VERSION"
		;;
	*)
		check_version "$INPUT_NEXT_VERSION" snapshot
		NEXT_SNAPSHOT="$INPUT_NEXT_VERSION-SNAPSHOT"
		;;
	esac
	BRANCH="$(git branch --show-current)"
	[ -n "$BRANCH" ] || error "not a branch on commit $COMMIT_ID"
	info "Prepare release '$REVISION', tag='$TAG', next snapshot='$NEXT_SNAPSHOT'"
	set_property revision "$REVISION"
	./mvnw -B --color=always -N versions:set-scm-tag "-DnewTag=$TAG"
	git add pom.xml
	git commit -m "Release $REVISION"
	git tag "$TAG"
	./mvnw -B --color=always -N versions:revert
	set_property revision "$NEXT_SNAPSHOT"
	git add pom.xml
	git commit -m "prepare for next development iteration"
	[ -n "$DRY_RUN" ] || git push --atomic origin "$TAG" "$BRANCH"
	git checkout "tags/$TAG"
}

release_perform() {
	# GITHUB_SHA might not point to the tag if the tag is generated in the previous step
	COMMIT_ID="$(git log -1 --pretty=%H)"
	TAG="$(git describe --exact-match --tags 2>/dev/null)" || error "There's no tag on commit $COMMIT_ID"
	REVISION="$(./mvnw -B -N help:evaluate -Dexpression=project.version -q -DforceStdout)"
	[ -n "$REVISION" ] || error "fail to get project.version"
	./mvnw -B --color=always -s .github/settings.xml -Prelease-profile deploy site-deploy
	find . -type f -name "*-$REVISION.zip" -print0 | xargs -0 gh release -R "$GITHUB_REPOSITORY" create "$TAG" --prerelease --target "$COMMIT_ID"
}

[ $# -eq 0 ] || {
	set -e
	set -o pipefail
	"$@"
}
