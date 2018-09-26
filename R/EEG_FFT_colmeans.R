library("R.matlab")
library("bmp")

#�d�ʐ�
channels <- 0

#���ԑ�
timeTotal <- 0

#�����V���[�Y
series <- 400

#��������(<399)
splitLimit <- 0

#�o�͉摜��
outWidth <- 183

#�o�͉摜��
outHeight <- 183

#���̓t�H���_�p�X�imat�j
#inputDir <- '/R/data'
inputDir <- '/R/data'

#�o�̓t�H���_�p�X(jpg)
outputDir <- '/R/output'

#�t�H���_�ő��݂���mat�t�@�C���𕪊����ďo��
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

#matLab�t�@�C���������ďo��
readMatFile <- function(path){
	setwd(inputDir)
	
	x <- readMat(path)
	
	#cat("-----------Orgin Data----------------------------------------\n")
	

	#mat�t�@�C���\���o��
	#print(x[[1]])

	# data �s��
	data <- x[[1]]
	channels = nrow(data)
	timeTotal = ncol(data)
	#cat(paste("row*col:",channels,"*",timeTotal,"\n\n"));
	
	setwd(outputDir)

		#�O���t�o�͗p�s��쐬
		outputData <- list()
		
		#���g��-���ω��Z�iX�F���ԁAY�F�U���j�F
		par(mfrow=c(1,1)) 
		#�]���̈�폜
		par(oma = c(0, 0, 0, 0)) 
		par(oma = c(0, 0, 0, 0)) 
		outputData <- colMeans(fft(data))
		plot(0:(length(outputData)-1),outputData,type="l",xlab="",ylab="",axes=FALSE)
		#�o�̓t�@�C����
		outputFile <- paste(substr(path, 1, nchar(path) - 4),".bmp",sep="")
		dev.copy(bmp,outputFile,width=outWidth,height=outHeight)
		dev.off()
		
		#�e�t�@�C���o��
		cat(paste("File",outputFile," OK!\n"))
	
		#���W�o��
		#cat("-----------Output Data-------------\n")
		#str(outputData)
}

#TEST�p�FBMP�t�@�C���Ǎ���
readBMP <- function(){	
	testimg <- read.bmp('/R/output/Dog_1_interictal_segment_0001_001.bmp')
	cat(dim(testimg))
	print(testimg)
}

readMatFolder()