library("R.matlab")
library("bmp")
library("e1071")

#電位数
channels <- 0

#時間帯
timeTotal <- 0

#分割シリーズ
series <- 400

#分割制限(<399)
splitLimit <- 0

#出力画像幅
outWidth <- 183

#出力画像高
outHeight <- 183

#入力フォルダパス（mat）
#inputDir <- '/R/data'
inputDir <- '/R/data'

#出力フォルダパス(jpg)
outputDir <- '/R/output'

#フォルダで存在するmatファイルを分割して出力
readMatFolder <- function(){
	setwd(inputDir)

	matfile <- dir()
	cnt <- grep("mat",matfile)
	
	#cat("-----------Split Data----------------------------------------\n")
	for ( i in 1:length(cnt)){
		path = matfile[cnt[i]];	
		print(path)
		readMatFile(path)		
	}

	cat(paste("---------------------------------------------------------------\n"))
	if(splitLimit != 0){
		cat(paste("Exported",splitLimit*length(cnt),"files!\n"))
	}
	setwd('/R')
	#source('/R/test01.R')
}

#matLabファイル分割して出力
readMatFile <- function(path){
	setwd(inputDir)
	
	x <- readMat(path)
	
	#cat("-----------Orgin Data----------------------------------------\n")
	

	#matファイル構成出力
	#print(x[[1]])

	# data 行列
	data <- x[[1]]
	channels = nrow(data)
	timeTotal = ncol(data)
	#cat(paste("row*col:",channels,"*",timeTotal,"\n\n"));
	
	setwd(outputDir)

		#グラフ出力用行列作成
		outputData <- list()
		
		#●時間周波数解析法
		par(mfrow=c(1,1)) 
		#余白領域削除
		par(oma = c(0, 0, 0, 0)) 
		par(oma = c(0, 0, 0, 0)) 
		#row1だけ（図抽出用）
		stftdata <- stft(data)
		plot(stftdata,xlab="",ylab="",axes=FALSE)
		#出力ファイル名
		outputFile <- paste(substr(path, 1, nchar(path) - 4),".bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		#各ファイル出力
		cat(paste("File",outputFile," OK!\n"))
	
		#座標出力
		#cat("-----------Output Data-------------\n")
		#str(outputData)
}

#TEST用：BMPファイル読込む
readBMP <- function(){	
	testimg <- read.bmp('/R/output/Dog_1_interictal_segment_0001_001.bmp')
	cat(dim(testimg))
	print(testimg)
}

readMatFolder()